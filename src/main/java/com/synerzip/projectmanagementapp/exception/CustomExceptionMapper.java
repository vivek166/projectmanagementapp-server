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
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 400, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		} 
		
		else if (exception instanceof NotFoundException) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 404, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		}
		
		else if (exception instanceof OverlappingFileLockException) {
			ErrorMessage errorMessage = new ErrorMessage("record already present", 409, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		}
		
		else if (exception instanceof ObjectNotFoundException) {
			ErrorMessage errorMessage = new ErrorMessage("no record found", 404, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		}
		
		else if (exception instanceof HibernateException) {
			ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 404, "https:github.com/vivek166");
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type("text/json").build();
		}
		
		else {
			return null;
		}
	}

}