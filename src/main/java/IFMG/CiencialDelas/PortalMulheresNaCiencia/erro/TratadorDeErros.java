package IFMG.CiencialDelas.PortalMulheresNaCiencia.erro;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Converte as exceções da API em respostas JSON com mensagem em português. */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroDTO> naoEncontrado(RecursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroDTO(HttpStatus.NOT_FOUND.value(), e.getMessage(), Map.of()));
    }

    /** Dados de formulário inválidos: 400 com a mensagem de cada campo, para o site mostrar ao lado do campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> dadosInvalidos(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : e.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ErroDTO(HttpStatus.BAD_REQUEST.value(), "Confira os campos destacados.", campos));
    }

    /** Corpo da requisição ausente ou que não é um JSON válido. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroDTO> corpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ErroDTO(HttpStatus.BAD_REQUEST.value(), "Não foi possível ler os dados enviados.", Map.of()));
    }
}
