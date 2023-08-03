package com.assets.management.assets.util;

import com.assets.management.assets.qualifier.WorkId;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@WorkId
@ApplicationScoped
public class WorkIdGenerator implements NumberGenerator {
//	private static AtomicLong sequence = new AtomicLong(1L);
//	public static int counter = 0;

    @Override
    public String generateNumber(Long id) {
        return String.format("UDSM-%d-%05d", LocalDate.now().getYear(), id);
//		return String.format("UDSM-%d-%05d", LocalDate.now().getYear(), sequence.getAndIncrement());
//		return String.format("UDSM-%d-%05d", LocalDate.now().getYear(), ++counter);
    }

}
