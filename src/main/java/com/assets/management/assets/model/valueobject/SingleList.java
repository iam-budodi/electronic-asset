package com.assets.management.assets.model.valueobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Transfer;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SingleList implements Serializable {
    private static final long serialVersionUID = 1L;
	private List<Allocation> firstList = new ArrayList<>();
    private List<Transfer> secondList;

    public SingleList(List<Allocation> firstList, List<Transfer> secondList) {
        this.firstList = firstList;
        this.secondList = secondList;
    }

	public List<Allocation> getFirstList() {
		return firstList;
	}

	public List<Transfer> getSecondList() {
		return secondList;
	}
    
//
//    @SuppressWarnings("unchecked")
//	public List<List<Object>> getLists() {
//        List<List<Object>> lists = new ArrayList<>();
//        lists.add(firstList);
//        lists.add(secondList);
//        return lists;
//    }
}

