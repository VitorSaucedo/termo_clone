package com.vitorsaucedo.wordle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.vitorsaucedo.wordle.service.LetterState.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("GuessEvaluatorService")
class GuessEvaluatorServiceTest {

    private GuessEvaluatorService evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new GuessEvaluatorService();
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private List<LetterState> states(String guess, String solution) {
        return evaluator.evaluate(guess, solution)
                .stream()
                .map(TileEvaluation::state)
                .toList();
    }

    private List<Character> letters(String guess, String solution) {
        return evaluator.evaluate(guess, solution)
                .stream()
                .map(TileEvaluation::letter)
                .toList();
    }

    // -------------------------------------------------------

    @Nested
    @DisplayName("Casos básicos")
    class BasicCases {

        @Test
        @DisplayName("Acerto total: todas as letras CORRECT")
        void allCorrect() {
            assertThat(states("TERMO", "TERMO"))
                    .containsExactly(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT);
        }

        @Test
        @DisplayName("Nenhuma letra em comum: todas ABSENT")
        void allAbsent() {
            assertThat(states("BRISA", "FOLHO"))
                    .containsExactly(ABSENT, ABSENT, ABSENT, ABSENT, ABSENT);
        }

        @Test
        @DisplayName("Letras certas, posições erradas: todas PRESENT")
        void allPresent() {
            // AMOR → solução ROMA: todas as letras existem, nenhuma no lugar certo
            assertThat(states("AMOR", "ROMA"))
                    .containsExactly(PRESENT, PRESENT, PRESENT, PRESENT);
        }

        @Test
        @DisplayName("Mix de CORRECT, PRESENT e ABSENT")
        void mixedResult() {
            // C-A-R-R-O vs T-E-R-M-O
            // C → ABSENT, A → ABSENT, R → ABSENT (já está em posição 4 na solução mas pos 2 tentativa…)
            // vamos usar um caso claro
            // CARRO vs BARCO: C(PRESENT) A(CORRECT) R(PRESENT) R(ABSENT) O(CORRECT)
            assertThat(states("CARRO", "BARCO"))
                    .containsExactly(PRESENT, CORRECT, PRESENT, ABSENT, CORRECT);
        }
    }

    // -------------------------------------------------------

    @Nested
    @DisplayName("Letras repetidas — o caso crítico")
    class RepeatedLetters {

        @Test
        @DisplayName("Tentativa tem mais ocorrências que a solução: excesso é ABSENT")
        void guesHasMoreOccurrencesThanSolution() {
            // Solução ARARA tem 3 A's. Tentativa AAAAA tem 5 A's.
            // Posições 0,2,4 são CORRECT (A nas posições certas de ARARA).
            // Posições 1,3 são ABSENT (sem A disponível sobrando na solução).
            assertThat(states("AAAAA", "ARARA"))
                    .containsExactly(CORRECT, ABSENT, CORRECT, ABSENT, CORRECT);
        }

        @Test
        @DisplayName("Clássico: RRRRR vs ARARA — apenas 2 R disponíveis na solução")
        void classicRepeatedConsonant() {
            // ARARA tem R nas posições 1 e 3.
            // RRRRR: pos 1 → CORRECT, pos 3 → CORRECT, demais → ABSENT
            assertThat(states("RRRRR", "ARARA"))
                    .containsExactly(ABSENT, CORRECT, ABSENT, CORRECT, ABSENT);
        }

        @Test
        @DisplayName("Letra repetida: uma ocorrência CORRECT e outra ABSENT")
        void oneCorrectOneAbsent() {
            // Solução CAMPO tem 1 C. Tentativa CACTO tem 2 C's.
            // C pos 0 → CORRECT (bate com C de CAMPO pos 0)
            // C pos 2 → ABSENT (não sobra C no pool da solução)
            assertThat(states("CACTO", "CAMPO"))
                    .containsExactly(CORRECT, CORRECT, ABSENT, ABSENT, ABSENT);
        }

        @Test
        @DisplayName("Letra repetida na tentativa: uma PRESENT, outra ABSENT")
        void onePresentOneAbsent() {
            // Solução CARTA tem 1 A. Tentativa BAIAO tem 2 A's.
            // Vamos usar: tentativa AABBB, solução XAXXX
            // A pos 0: solução[0]=X → não bate. Pool: X=1, A=1, X=1...
            // Melhor caso controlado:
            // tentativa LLAMA, solução LLANO
            // L(0) CORRECT, L(1) CORRECT, A(2) CORRECT, M(3) ABSENT, A(4) ABSENT
            // solução LLANO tem 1 A na pos 2. tentativa LLAMA: A em pos 2 (CORRECT),
            // A em pos 4 → pool de A já zerado → ABSENT
            assertThat(states("LLAMA", "LLANO"))
                    .containsExactly(CORRECT, CORRECT, CORRECT, ABSENT, ABSENT);
        }

        @Test
        @DisplayName("Duas ocorrências na solução: ambas marcadas corretamente")
        void twoOccurrencesInSolution() {
            // Solução BOBO tem 2 B's e 2 O's
            // Tentativa OBOB: O(PRESENT) B(PRESENT) O(PRESENT) B(PRESENT)
            assertThat(states("OBOB", "BOBO"))
                    .containsExactly(PRESENT, PRESENT, PRESENT, PRESENT);
        }

        @Test
        @DisplayName("CORRECT tem prioridade: não deve ser re-consumido como PRESENT")
        void correctTakesPriorityOverPresent() {
            // Solução SPEED (inglês para testar o algoritmo puro)
            // Tentativa EERIE: E(PRESENT) E(CORRECT) R(ABSENT) I(ABSENT) E(ABSENT)
            // SPEED tem E nas pos 1,2 (EE). EERIE tem E nas pos 0,1,4.
            // Pass1: pos1 E==E → CORRECT. Pool restante: S=1,P=1,E=1,D=1
            // Pass2: pos0 E → pool[E]=1 → PRESENT, decrementa. pos4 E → pool[E]=0 → ABSENT
            assertThat(states("EERIE", "SPEED"))
                    .containsExactly(PRESENT, CORRECT, ABSENT, ABSENT, ABSENT);
        }
    }

    // -------------------------------------------------------

    @Nested
    @DisplayName("Normalização de entrada")
    class Normalization {

        @Test
        @DisplayName("Tentativa em minúsculas deve funcionar igual a maiúsculas")
        void lowercaseGuess() {
            assertThat(states("termo", "TERMO"))
                    .containsExactly(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT);
        }

        @Test
        @DisplayName("Solução em minúsculas deve funcionar igual a maiúsculas")
        void lowercaseSolution() {
            assertThat(states("TERMO", "termo"))
                    .containsExactly(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT);
        }

        @Test
        @DisplayName("Letras retornadas devem estar sempre em maiúsculas")
        void returnedLettersAreUppercase() {
            assertThat(letters("termo", "termo"))
                    .containsExactly('T', 'E', 'R', 'M', 'O');
        }
    }

    // -------------------------------------------------------

    @Nested
    @DisplayName("Tamanhos variados de palavra")
    class WordLengths {

        @Test
        @DisplayName("Palavra de 4 letras")
        void fourLetterWord() {
            assertThat(states("AMOR", "AMOR"))
                    .containsExactly(CORRECT, CORRECT, CORRECT, CORRECT);
        }

        @Test
        @DisplayName("Palavra de 6 letras")
        void sixLetterWord() {
            assertThat(states("QUADRO", "QUADRO"))
                    .containsExactly(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT, CORRECT);
        }
    }

    // -------------------------------------------------------

    @Nested
    @DisplayName("Validações de entrada")
    class InputValidation {

        @Test
        @DisplayName("Tamanhos diferentes devem lançar IllegalArgumentException")
        void differentLengthsThrowException() {
            assertThatThrownBy(() -> evaluator.evaluate("TERMO", "QUADRO"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("mesmo tamanho");
        }

        @Test
        @DisplayName("Strings vazias lançam IllegalArgumentException")
        void emptyStringsThrowException() {
            assertThatThrownBy(() -> evaluator.evaluate("", "TERMO"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------

    @Nested
    @DisplayName("Resultado é uma lista de Records imutáveis")
    class RecordContract {

        @Test
        @DisplayName("Retorna exatamente N TileEvaluations para palavra de N letras")
        void returnsSizeEqualToWordLength() {
            List<TileEvaluation> result = evaluator.evaluate("TERMO", "BARCO");
            assertThat(result).hasSize(5);
        }

        @Test
        @DisplayName("Cada TileEvaluation contém a letra e o estado corretos")
        void tileEvaluationContainsLetterAndState() {
            List<TileEvaluation> result = evaluator.evaluate("TERMO", "TERMO");

            assertThat(result.get(0).letter()).isEqualTo('T');
            assertThat(result.get(0).state()).isEqualTo(CORRECT);

            assertThat(result.get(4).letter()).isEqualTo('O');
            assertThat(result.get(4).state()).isEqualTo(CORRECT);
        }
    }
}