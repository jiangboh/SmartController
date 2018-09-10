/*
 * Copyright 2015 Yan Zhenjie
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
package com.magnet.no_http.able;

import java.util.concurrent.BlockingQueue;

/**
 * <p>Queue interface.</p>
 * Created in Nov 12, 2015 5:59:29 PM.
 *
 * @author Yan Zhenjie;
 */
public interface Queueable {

    /**
     * Set the request in the queue.
     *
     * @param queue queue.
     */
    void setQueue(BlockingQueue<?> queue);

    /**
     * In the queue?
     *
     * @return true: in the queue, false: not in the queue.
     */
    boolean inQueue();

}
