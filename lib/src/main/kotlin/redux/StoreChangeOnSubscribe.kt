package redux

import redux.Store.Subscriber
import rx.Observable

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

class StoreChangeOnSubscribe<S : Any>(private val store: Store<S>) : Observable.OnSubscribe<S> {

    override fun call(subscriber: rx.Subscriber<in S>) {
        val subscription = store.subscribe(object : Subscriber {
            override fun onStateChanged() {
                if (!subscriber.isUnsubscribed) {
                    subscriber.onNext(store.getState())
                }
            }
        })

        subscriber.add(object : StoreChangeSubscription() {
            override fun onUnsubscribe() {
                subscription.unsubscribe()
            }
        })
    }

}
