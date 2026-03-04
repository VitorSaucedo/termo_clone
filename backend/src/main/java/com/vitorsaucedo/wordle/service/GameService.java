package com.vitorsaucedo.wordle.service;

import com.vitorsaucedo.wordle.api.dto.*;
import com.vitorsaucedo.wordle.domain.*;
import com.vitorsaucedo.wordle.exception.*;
import com.vitorsaucedo.wordle.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private static final String DEFAULT_LANGUAGE = "pt-BR";

    private final DailyWordService      dailyWordService;
    private final WordService           wordService;
    private final GuessEvaluatorService evaluatorService;
    private final StatsService          statsService;
    private final GameSessionRepository sessionRepository;
    private final SessionCreatorService sessionCreatorService;

    @Transactional
    public DailyGameResponse getDailyGame(String userId) {
        DailyWord dailyWord = dailyWordService.getTodaysWord(GameMode.CLASSIC, DEFAULT_LANGUAGE);
        GameSession session = findOrCreate(userId, dailyWord);
        return toResponse(session, dailyWord);
    }

    @Transactional
    public GuessResponse submitGuess(GuessRequest request) {
        GameSession session = sessionRepository
                .findByIdWithWord(request.sessionId())
                .orElseThrow(() -> new SessionNotFoundException(request.sessionId()));

        if (session.isFinished()) throw new GameAlreadyFinishedException(request.sessionId());

        String guess    = request.guess().toUpperCase();
        String solution = session.getDailyWord().getWord().getWord().toUpperCase();

        if (!wordService.isValidWord(guess, DEFAULT_LANGUAGE))
            throw new InvalidWordException(guess);

        var evaluations = evaluatorService.evaluate(guess, solution);
        session.addAttempt(guess, solution);
        sessionRepository.save(session);

        if (session.isFinished()) statsService.updateStats(session);

        return new GuessResponse(
                evaluations,
                session.getAttempts().size(),
                session.getStatus(),
                session.getStatus() == GameStatus.LOST ? solution : null
        );
    }

    @Transactional
    public MultiGameResponse getMultiGame(String userId, GameMode mode) {
        List<DailyWord> dailyWords = dailyWordService.getTodaysWords(mode, DEFAULT_LANGUAGE);
        List<GameSession> sessions = dailyWords.stream()
                .map(dw -> findOrCreate(userId, dw))
                .toList();

        int attemptsMade = sessions.stream()
                .mapToInt(s -> s.getAttempts().size())
                .max().orElse(0);

        List<DailyGameResponse> grids = sessions.stream()
                .map(s -> toResponse(s, s.getDailyWord()))
                .toList();

        return new MultiGameResponse(mode, grids, computeGlobalStatus(sessions), attemptsMade, sessions.get(0).maxAttempts());
    }

    @Transactional
    public MultiGuessResponse submitMultiGuess(MultiGuessRequest request) {
        List<GameSession> sessions = request.sessionIds().stream()
                .map(id -> sessionRepository.findByIdWithWord(id)
                        .orElseThrow(() -> new SessionNotFoundException(id)))
                .toList();

        String guess = request.guess().toUpperCase();

        if (!wordService.isValidWord(guess, DEFAULT_LANGUAGE))
            throw new InvalidWordException(guess);

        List<MultiGuessResponse.GridResult> results = sessions.stream()
                .map(session -> {
                    String solution = session.getDailyWord().getWord().getWord().toUpperCase();
                    var evaluations = evaluatorService.evaluate(guess, solution);

                    if (!session.isFinished()) {
                        session.addAttempt(guess, solution);
                        sessionRepository.save(session);
                        if (session.isFinished()) statsService.updateStats(session);
                    }

                    return new MultiGuessResponse.GridResult(
                            session.getId(),
                            evaluations,
                            session.getStatus(),
                            session.getStatus() == GameStatus.LOST ? solution : null
                    );
                })
                .toList();

        int attemptsMade = sessions.stream()
                .mapToInt(s -> s.getAttempts().size())
                .max().orElse(0);

        return new MultiGuessResponse(results, attemptsMade, computeGlobalStatus(sessions));
    }

    private DailyGameResponse toResponse(GameSession session, DailyWord dailyWord) {
        String solution = dailyWord.getWord().getWord().toUpperCase();
        List<List<TileEvaluation>> pastEvals = session.getAttempts().stream()
                .map(g -> evaluatorService.evaluate(g, solution))
                .toList();

        return new DailyGameResponse(
                session.getId(),
                dailyWord.getWord().getLength(),
                session.maxAttempts(),
                session.getAttempts().size(),
                List.copyOf(session.getAttempts()),
                pastEvals,
                session.getStatus(),
                session.getMode()
        );
    }

    private GameSession findOrCreate(String userId, DailyWord dailyWord) {
        return sessionRepository
                .findByUserIdAndDailyWordId(userId, dailyWord.getId())
                .orElseGet(() -> {
                    try {
                        return sessionCreatorService.createSession(userId, dailyWord);
                    } catch (DataIntegrityViolationException e) {
                        log.debug("Race condition — buscando sessão existente para user={}", userId);
                        return sessionRepository
                                .findByUserIdAndDailyWordId(userId, dailyWord.getId())
                                .orElseThrow(() -> new IllegalStateException(
                                        "Sessão não encontrada após conflito para usuário: " + userId));
                    }
                });
    }

    private GameStatus computeGlobalStatus(List<GameSession> sessions) {
        boolean allWon  = sessions.stream().allMatch(s -> s.getStatus() == GameStatus.WON);
        boolean anyLost = sessions.stream().anyMatch(s -> s.getStatus() == GameStatus.LOST);
        if (allWon)  return GameStatus.WON;
        if (anyLost) return GameStatus.LOST;
        return GameStatus.PLAYING;
    }
}