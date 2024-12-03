package at.tree.challenge.api

import at.tree.challenge.api.dto.ErrorResponse
import at.tree.challenge.exception.EdgeExistsException
import at.tree.challenge.exception.EdgeNotFoundException
import at.tree.challenge.exception.InternalErrorException
import at.tree.challenge.exception.LoopException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {
    @ExceptionHandler
    fun handleEdgeExistsException(ex: EdgeExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Edge already exists."), HttpStatus.CONFLICT)
    }

    @ExceptionHandler
    fun handleEdgeNotFoundException(ex: EdgeNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Edge doesn't exists."), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handleLoopException(ex: LoopException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Loop detected. Edge wasn't created."), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handleInternalErrorException(ex: InternalErrorException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("Something went wrong while processing the request. Please try again (later)."),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
