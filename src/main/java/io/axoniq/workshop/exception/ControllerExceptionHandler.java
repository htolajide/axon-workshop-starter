package io.axoniq.workshop.exception;


import java.time.ZonedDateTime;

import io.axoniq.workshop.shared.ErrorMessage;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * This class defines exception handling controller.
 * @author Taofeek Hammed
 * @since 12th January 2023
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage illegalStateExceptionHandler(IllegalStateException ex) {
		return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.toString(), ex.getMessage(), ZonedDateTime.now());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessage serverExceptionHandler(Exception ex) {
		ex.printStackTrace();
		return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.toString(), ex.getMessage(), ZonedDateTime.now());
	}

}
