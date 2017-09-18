package redux

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.functions.Consumer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import redux.helpers.ActionCreators.unknownAction
import redux.helpers.Reducers
import redux.helpers.Todos.State

/*
 * Copyright (C) 2016 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@RunWith(JUnitPlatform::class)
class StoresTest : Spek({

    describe("Store") {

        describe("asObservable") {

            it("creates a state change observable") {
                val store = createStore(Reducers.TODOS, State())
                val consumer = mock(Consumer::class.java) as Consumer<State>

                store.asObservable().subscribe(consumer)
                store.dispatch(unknownAction())
                verify(consumer, times(1)).accept(any<State>())
            }

            it("creates a cold observable") {
                val store = createStore(Reducers.TODOS, State())
                val consumerA = mock(Consumer::class.java) as Consumer<State>
                val consumerB = mock(Consumer::class.java) as Consumer<State>
                val storeChangeStream = store.asObservable().share()

                verify(consumerA, times(0)).accept(any<State>())
                verify(consumerB, times(0)).accept(any<State>())

                storeChangeStream.subscribe(consumerA)
                store.dispatch(unknownAction())
                verify(consumerA, times(1)).accept(any<State>())
                verify(consumerB, times(0)).accept(any<State>())

                storeChangeStream.subscribe(consumerB)
                store.dispatch(unknownAction())
                verify(consumerA, times(2)).accept(any<State>())
                verify(consumerB, times(1)).accept(any<State>())
            }

            it("skips disposed subscriptions") {
                val store = createStore(Reducers.TODOS, State())
                val consumer = mock(Consumer::class.java) as Consumer<State>
                val subscription = store.asObservable().subscribe(consumer)

                store.dispatch(unknownAction())
                verify(consumer, times(1)).accept(any<State>())

                subscription.dispose()

                store.dispatch(unknownAction())
                verify(consumer, times(1)).accept(any<State>())
            }

        }

    }

})
