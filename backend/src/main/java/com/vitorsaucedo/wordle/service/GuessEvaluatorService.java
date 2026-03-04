package com.vitorsaucedo.wordle.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GuessEvaluatorService {

    public List<TileEvaluation> evaluate(String guess, String solution) {
        final String g = guess.toUpperCase();
        final String s = solution.toUpperCase();

        if (g.length() != s.length()) {
            throw new IllegalArgumentException(
                    "Tentativa e solução devem ter o mesmo tamanho. " +
                            "Tentativa: %d, Solução: %d".formatted(g.length(), s.length())
            );
        }

        final int len = s.length();
        final LetterState[] result = new LetterState[len];

        final Map<Character, Integer> solutionPool = new HashMap<>();

        for (int i = 0; i < len; i++) {
            if (g.charAt(i) == s.charAt(i)) {
                result[i] = LetterState.CORRECT;
            } else {
                solutionPool.merge(s.charAt(i), 1, Integer::sum);
            }
        }

        for (int i = 0; i < len; i++) {
            if (result[i] == LetterState.CORRECT) continue;

            final char c = g.charAt(i);
            final int available = solutionPool.getOrDefault(c, 0);
            if (available > 0) {
                result[i] = LetterState.PRESENT;
                solutionPool.put(c, available - 1);
            } else {
                result[i] = LetterState.ABSENT;
            }
        }

        final List<TileEvaluation> evaluations = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            evaluations.add(new TileEvaluation(g.charAt(i), result[i]));
        }

        return evaluations;
    }
}