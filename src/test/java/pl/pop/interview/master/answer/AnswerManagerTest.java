package pl.pop.interview.master.answer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pop.interview.master.question.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerManagerTest {
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuestionFacade questionService;
    @InjectMocks
    private AnswerManager answerManager;

    @Test
    public void testFindRandomQuestion() {
        Question question = new Question();
        QuestionDTO questionDTO2 = new QuestionDTO();
        QuestionDTO questionDTO = new QuestionDTO();
        questionRepository.save(question);
        when(questionRepository.findRandomQuestion()).thenReturn(Optional.of(question));
        when(questionService.mapToDto(question)).thenReturn(questionDTO);
        assertSame(questionDTO, answerManager.findRandomQuestion());
        assertNotSame(questionDTO2, answerManager.findRandomQuestion());
    }

    @Test
    public void testSaveNewCorrectAnswer() {
        Question question1 = new Question("Question1Content", true);
        Answer answerCorrect = new Answer();
        answerCorrect.setQuestionContent("Question1Content");
        answerCorrect.setCorrect(true);
        answerCorrect.setResult("Correct answer");
        AnswerDTO answerDTOCorrect = new AnswerDTO();
        answerDTOCorrect.setResult("Correct answer");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question1));
        when(answerRepository.save(any())).thenReturn(answerCorrect);

        AnswerDTO resultCorrect = answerManager.save(1L, true);

        ArgumentCaptor<Answer> answerCaptor = ArgumentCaptor.forClass(Answer.class);
        verify(answerRepository).save(answerCaptor.capture());
        Answer savedAnswer = answerCaptor.getValue();

        assertEquals(question1.getContent(), savedAnswer.getQuestionContent());
        assertEquals(question1.isCorrectAnswer(), savedAnswer.isCorrect());
        assertEquals(question1.getContent(), resultCorrect.getQuestion());
        assertEquals(question1.isCorrectAnswer(), resultCorrect.isCorrect());
    }

    @Test
    public void testSaveIncorrectAnswer() {
        Question question1 = new Question("QuestionContent", true);
        Answer answerIncorrect = new Answer();
        answerIncorrect.setQuestionContent("QuestionContent");
        answerIncorrect.setCorrect(false);
        answerIncorrect.setResult("Incorrect answer or answer format Yes/No");
        AnswerDTO answerDTOIncorrect = new AnswerDTO();
        answerDTOIncorrect.setResult("Incorrect answer or answer format Yes/No");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question1));
        when(answerRepository.save(any())).thenReturn(answerIncorrect);

        AnswerDTO resultIncorrect = answerManager.save(1L, false);

        ArgumentCaptor<Answer> answerCaptor = ArgumentCaptor.forClass(Answer.class);
        verify(answerRepository).save(answerCaptor.capture());
        Answer savedAnswer = answerCaptor.getValue();

        assertEquals(question1.getContent(), savedAnswer.getQuestionContent());
        assertNotEquals(question1.isCorrectAnswer(), savedAnswer.isCorrect());
        assertEquals(question1.getContent(), resultIncorrect.getQuestion());
        assertNotEquals(question1.isCorrectAnswer(), resultIncorrect.isCorrect());
    }
}