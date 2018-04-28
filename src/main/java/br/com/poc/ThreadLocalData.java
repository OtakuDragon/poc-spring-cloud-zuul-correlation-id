package br.com.poc;

public class ThreadLocalData {
	public static ThreadLocal<String> correlationId = new ThreadLocal<>();
}
