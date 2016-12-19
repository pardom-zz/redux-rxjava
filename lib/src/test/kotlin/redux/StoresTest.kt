package redux

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import redux.helpers.ActionCreators.unknownAction
import redux.helpers.Reducers
import redux.helpers.Todos.State
import rx.functions.Action1

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
                val onNext = mock(Action1::class.java) as Action1<State>

                store.asObservable().subscribe(onNext)
                store.dispatch(unknownAction())
                verify(onNext, times(1)).call(any())
            }

            it("creates a cold observable") {
                val store = createStore(Reducers.TODOS, State())
                val onNextA = mock(Action1::class.java) as Action1<State>
                val onNextB = mock(Action1::class.java) as Action1<State>
                val storeChangeStream = store.asObservable().share()

                verify(onNextA, times(0)).call(any())
                verify(onNextB, times(0)).call(any())

                storeChangeStream.subscribe(onNextA)
                store.dispatch(unknownAction())
                verify(onNextA, times(1)).call(any())
                verify(onNextB, times(0)).call(any())

                storeChangeStream.subscribe(onNextB)
                store.dispatch(unknownAction())
                verify(onNextA, times(2)).call(any())
                verify(onNextB, times(1)).call(any())
            }

            it("skips unsubscribed subscriptions") {
                val store = createStore(Reducers.TODOS, State())
                val onNext = mock(Action1::class.java) as Action1<State>
                val subscription = store.asObservable().subscribe(onNext)

                store.dispatch(unknownAction())
                verify(onNext, times(1)).call(any())

                subscription.unsubscribe()

                store.dispatch(unknownAction())
                verify(onNext, times(1)).call(any())
            }

        }

    }

})
