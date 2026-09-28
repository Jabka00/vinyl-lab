package dev.vinyllab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ModelAndView notFound(NotFoundException exception) {
    return page("error/404", exception.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ModelAndView conflict(ConflictException exception) {
    return page("error/conflict", exception.getMessage());
  }

  private ModelAndView page(String view, String message) {
    ModelAndView model = new ModelAndView(view);
    model.addObject("message", message);
    return model;
  }
}
