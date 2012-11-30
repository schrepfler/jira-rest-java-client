/*
 * Copyright (C) 2011 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.domain;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Basic representation of a JIRA issues and errors created using batch operation.
 *
 * @since v1.1
 */
public class BasicIssues
{

    private final Collection<BasicIssue> issues;
    private final Collection<BulkCreateErrorResult> errors;

    public BasicIssues(final Collection<BasicIssue> issues, final Collection<BulkCreateErrorResult> errors)
    {
        this.issues = (issues != null) ? issues : Collections.<BasicIssue>emptyList();
        this.errors = (errors != null) ? errors : Collections.<BulkCreateErrorResult>emptyList();
    }

    public Collection<BasicIssue> getIssues()
    {
        return issues;
    }

    public Collection<BulkCreateErrorResult> getErrors()
    {
        return errors;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("issues", Arrays.deepToString(issues.toArray()))
                .add("errors", Arrays.deepToString(errors.toArray()))
                .toString();

    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof BasicIssues)
        {
            final BasicIssues that = (BasicIssues) obj;
            return Objects.equal(this.issues, that.issues)
                    && Objects.equal(this.errors, that.errors);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(issues, errors);
    }
}
