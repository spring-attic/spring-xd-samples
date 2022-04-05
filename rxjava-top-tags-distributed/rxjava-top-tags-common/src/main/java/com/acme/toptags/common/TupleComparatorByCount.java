/*
 * Copyright 2015 the original author or authors.
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

package com.acme.toptags.common;

import java.util.Comparator;

import org.springframework.xd.tuple.Tuple;

/**
 * @author Marius Bogoevici
 */
public class TupleComparatorByCount implements Comparator<Tuple> {

	public static final TupleComparatorByCount INSTANCE = new TupleComparatorByCount();

	@Override
	public int compare(Tuple o1, Tuple o2) {
		return - Integer.valueOf(o1.getInt(Keys.COUNT)).compareTo(o2.getInt(Keys.COUNT));
	}

}
