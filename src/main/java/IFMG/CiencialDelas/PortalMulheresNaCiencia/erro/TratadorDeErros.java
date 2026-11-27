package IFMG.CiencialDelas.PortalMulheresNaCiencia.erro;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Converte as exceções da API em respostas JSON com mensagem em português. */
@RestControllerAdvice
public class TratadorDeErros {

    private static final String CONFIRA_OS_CAMPOS = "Confira os campos destacados.";

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
                .body(new ErroDTO(HttpStatus.BAD_REQUEST.value(), CONFIRA_OS_CAMPOS, campos));
    }

    /** Regra de negócio que não passou (por exemplo, área que não existe): 400 com a mensagem no campo. */
    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroDTO> regraNaoAtendida(DadosInvalidosException e) {
        return ResponseEntity.badRequest()
                .body(new ErroDTO(HttpStatus.BAD_REQUEST.value(), CONFIRA_OS_CAMPOS, Map.of(e.getCampo(), e.getMessage())));
    }

    /** Corpo da requisição ausente ou que não é um JSON válido. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroDTO> corpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ErroDTO(HttpStatus.BAD_REQUEST.value(), "Não foi possível ler os dados enviados.", Map.of()));
    }

    /** Parâmetro de endereço com valor inválido (por exemplo, ?status=QUALQUER). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroDTO> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(new ErroDTO(HttpStatus.BAD_REQUEST.value(), "Valor inválido em " + e.getName() + ".", Map.of()));
    }

    /** E-mail ou senha errados no login. A mensagem não diz qual dos dois, por segurança. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroDTO> loginRecusado(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroDTO(HttpStatus.UNAUTHORIZED.value(), "E-mail ou senha incorretos.", Map.of()));
    }
}
