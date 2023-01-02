package com.assets.management.assets.util;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;

import com.assets.management.assets.qualifier.WorkId;

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
