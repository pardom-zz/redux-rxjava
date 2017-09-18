package redux

import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

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

abstract class StoreChangeDisposable : Disposable {

    private val disposed = AtomicBoolean()

    protected abstract fun onDispose()

    override fun isDisposed() = disposed.get()

    override fun dispose() {
        if (disposed.compareAndSet(false, true)) {
            onDispose()
        }
    }

}
