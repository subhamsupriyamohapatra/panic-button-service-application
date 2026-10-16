package com.emergency.panicbutton.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * A {@link PageRequest} that maps FastAPI's {@code skip}/{@code limit} semantics
 * onto Spring Data's offset-based paging (instead of page/size).
 */
public class OffsetBasedPageRequest extends PageRequest {

    private final long offset;

    public OffsetBasedPageRequest(long offset, int limit, Sort sort) {
        super(0, limit, sort);
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative");
        }
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
