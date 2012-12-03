package com.atlassian.jira.rest.client.domain.util;

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @since v5.2
 */
public class ErrorCollection {

	private final Integer status;
	private final Collection<String> errorMessages;
	private final Map<String, String> errors;

	public ErrorCollection(final Integer status, final Collection<String> errorMessages, final Map<String, String> errors) {
		this.status = status;
		this.errors = errors;
		this.errorMessages = errorMessages;
	}

	public Integer getStatus() {
		return status;
	}

	public Collection<String> getErrorMessages() {
		return errorMessages;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
				.add("status", status)
				.add("errors", errors)
				.add("errorMessages", errorMessages)
				.toString();

	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof ErrorCollection)
		{
			final ErrorCollection that = (ErrorCollection) obj;
			return Objects.equal(this.status, that.status)
					&& Objects.equal(this.errors, that.errors)
					&& Objects.equal(this.errorMessages, that.errorMessages);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(status, errors, errorMessages);
	}
}
