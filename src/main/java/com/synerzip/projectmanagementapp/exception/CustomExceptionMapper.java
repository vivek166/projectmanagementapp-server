package com.synerzip.projectmanagementapp.exception;

import java.nio.channels.OverlappingFileLockException;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;

import com.synerzip.projectmanagementapp.model.ErrorMessage;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		if (exception instanceof EntityNotFoundException) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 422, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof NotFoundException) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 404, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof OverlappingFileLockException) {
			ErrorMessage errorMessage = new ErrorMessage("record already present", 409, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof ObjectNotFoundException) {
			ErrorMessage errorMessage = new ErrorMessage("no record found", 400, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof HibernateException) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 501, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof FieldCanNotEmpty) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 304, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof MediaTypeException) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 415, "https:github.com/vivek166");
			return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof CompanyAlreadyPresent) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 409, "https:github.com/vivek166");
			return Response.status(Status.NOT_MODIFIED).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof UserAlreadyPresent) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 409, "https:github.com/vivek166");
			return Response.status(Status.NOT_MODIFIED).entity(errorMessage).type("text/json").build();
		} else if (exception instanceof CanNotChangePassword) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 409, "https:github.com/vivek166");
			return Response.status(Status.NOT_MODIFIED).entity(errorMessage).type("text/json").build();
		} else {
			ErrorMessage errorMessage = new ErrorMessage("internal server error", 505, "https:github.com/vivek166");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).type("text/json").build();
		}
	}

}