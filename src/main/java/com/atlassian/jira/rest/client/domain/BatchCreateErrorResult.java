package com.atlassian.jira.rest.client.domain;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents error of creating single element during batch operation.
 *
 * @since v1.1
 */

public class BatchCreateErrorResult
{
    private final Integer status;
    private final Collection<String> errorMessages;
    private final Integer failedElementNumber;

    public BatchCreateErrorResult(final Integer status, final Collection<String> errorMessages, final Integer failedElementNumber)
    {
        this.status = status;
        this.errorMessages = (errorMessages != null) ? errorMessages : Collections.<String>emptyList();
        this.failedElementNumber = failedElementNumber;
    }

    public Integer getStatus()
    {
        return status;
    }

    public Collection<String> getErrorMessages()
    {
        return errorMessages;
    }

    public Integer getFailedElementNumber()
    {
        return failedElementNumber;
    }


    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("issues", status)
                .add("issues", Arrays.deepToString(errorMessages.toArray()))
                .add("errors", failedElementNumber)
                .toString();

    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof BatchCreateErrorResult)
        {
            final BatchCreateErrorResult that = (BatchCreateErrorResult) obj;
            return Objects.equal(this.status, that.status)
                    && Objects.equal(this.errorMessages, that.errorMessages)
                    && Objects.equal(this.failedElementNumber, that.failedElementNumber);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(status, errorMessages, failedElementNumber);
    }
}
