/*package com.synerzip.projectmanagementapp.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.synerzip.projectmanagementapp.model.ErrorMessage;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {
		ErrorMessage errorMessage = new ErrorMessage("internal server error", 500, "https:github.com/vivek166");
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).type("text/json").build();
	}

}*/