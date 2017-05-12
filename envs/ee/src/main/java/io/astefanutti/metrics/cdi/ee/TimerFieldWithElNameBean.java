/**
 * Copyright © 2013 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.astefanutti.metrics.cdi.ee;

import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Metric;

import javax.inject.Inject;

public class TimerFieldWithElNameBean {

    @Inject
    @Metric(name = "timer ${timerIdBean.id}")
    Timer timerWithName;

    @Inject
    @Metric(name = "timer ${timerIdBean.id} is absolute", absolute = true)
    Timer timerWithAbsoluteName;
}
