package com.atlassian.jira.rest.client.domain;

import com.google.common.base.Objects;

import java.util.Map;

/**
 * Represents error of creating single element during batch operation.
 *
 * @since v1.1
 */

public class BulkOperationErrorResult
{
    private final Integer status;
    private final Map<String,String> errorMessages;
    private final Integer failedElementNumber;

    public BulkOperationErrorResult(final Integer status, final Map<String,String> errorMessages, final Integer failedElementNumber)
    {
        this.status = status;
        this.errorMessages = errorMessages;
        this.failedElementNumber = failedElementNumber;
    }

    public Integer getStatus()
    {
        return status;
    }

    public Map<String,String> getErrorMessages()
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
                .add("status", status)
                .add("errorMessages", errorMessages)
                .add("failedElementNumber", failedElementNumber)
                .toString();

    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof BulkOperationErrorResult)
        {
            final BulkOperationErrorResult that = (BulkOperationErrorResult) obj;
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
