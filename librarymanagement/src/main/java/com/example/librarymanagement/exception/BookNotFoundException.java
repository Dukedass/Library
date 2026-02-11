package com.example.librarymanagement.exception;

public class BookNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2108453626119943296L;

	public BookNotFoundException(String message) {
		super(message);
	}

}
