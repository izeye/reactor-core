/*
 * Copyright (c) 2011-2016 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;

import org.junit.Assert;
import org.junit.Test;
import org.testng.collections.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.Fuseable;
import reactor.core.scheduler.Schedulers;
import reactor.test.TestSubscriber;

import static reactor.core.Fuseable.ANY;
import static reactor.core.Fuseable.ASYNC;
import static reactor.core.Fuseable.SYNC;

public class FluxMapOrFilterTest {

	@Test
	public void normal() {
		TestSubscriber<Integer> ts = TestSubscriber.create();
		
		Flux.range(1, 5).mapOrFilter(v -> v * 2).subscribe(ts);

		Set<Integer> expectedValues = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));
		ts.assertContainValues(expectedValues)
		.assertNoError()
		.assertComplete();
	}

	@Test
	public void filterNullMapResult() {
		TestSubscriber<Integer> ts = TestSubscriber.create();

		Flux.range(1, 5).mapOrFilter(v -> v % 2 == 0 ? v * 2 : null).subscribe(ts);

		Set<Integer> expectedValues = new HashSet<>(Arrays.asList(4, 8));
		ts.assertContainValues(expectedValues)
			.assertNoError()
			.assertComplete();
	}

	@Test
	public void normalSyncFusion() {
		TestSubscriber<Integer> ts = TestSubscriber.create();
		ts.requestedFusionMode(SYNC);

		Flux.range(1, 5).mapOrFilter(v -> v * 2).subscribe(ts);

		Set<Integer> expectedValues = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));
		ts.assertContainValues(expectedValues)
			.assertNoError()
			.assertComplete()
		  .assertFuseableSource()
		  .assertFusionMode(SYNC);
	}

	@Test
	public void normalAsyncFusion() {
		TestSubscriber<Integer> ts = TestSubscriber.create();
		ts.requestedFusionMode(ASYNC);

		Flux.range(1, 5).mapOrFilter(v -> v * 2).publishOn(Schedulers.single()).subscribe(ts);

		Set<Integer> expectedValues = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));
		ts.await()
		  .assertContainValues(expectedValues)
			.assertNoError()
			.assertComplete()
			.assertFuseableSource()
			.assertFusionMode(ASYNC);
	}

	@Test
	public void filterNullMapResultSyncFusion() {
		TestSubscriber<Integer> ts = TestSubscriber.create();
		ts.requestedFusionMode(SYNC);

		Flux.range(1, 5).mapOrFilter(v -> v % 2 == 0 ? v * 2 : null).subscribe(ts);

		Set<Integer> expectedValues = new HashSet<>(Arrays.asList(4, 8));
		ts.assertContainValues(expectedValues)
			.assertNoError()
			.assertComplete()
		  .assertFuseableSource()
			.assertFusionMode(SYNC);
	}

	@Test
	public void filterNullMapResultAsyncFusion() {
		TestSubscriber<Integer> ts = TestSubscriber.create();
		ts.requestedFusionMode(ASYNC);

		Flux.range(1, 5).mapOrFilter(v -> v % 2 == 0 ? v * 2 : null).publishOn(Schedulers.single()).subscribe(ts);

		Set<Integer> expectedValues = new HashSet<>(Arrays.asList(4, 8));
		ts.await()
			.assertContainValues(expectedValues)
			.assertNoError()
			.assertComplete()
			.assertFuseableSource()
			.assertFusionMode(ASYNC);
	}

}
