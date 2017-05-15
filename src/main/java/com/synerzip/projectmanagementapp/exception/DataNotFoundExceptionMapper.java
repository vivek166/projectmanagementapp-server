package com.synerzip.projectmanagementapp.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.synerzip.projectmanagementapp.model.ErrorMessage;

@Provider
public final class DataNotFoundExceptionMapper implements ExceptionMapper<DataNotFoundException> {

	@Override
	public Response toResponse(final DataNotFoundException exception) {
		ErrorMessage errorMessage=new ErrorMessage(exception.getMessage(), 404 ,"https:github.com/vivek166");
		return Response.status(Status.NOT_FOUND)
				.entity(errorMessage)
				.build();
	}

}
