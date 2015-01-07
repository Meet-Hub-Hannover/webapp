/**
    This file is part of Meet-Hub-Hannover.

    Meet-Hub-Hannover is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Meet-Hub-Hannover is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Meet-Hub-Hannover. If not, see <http://www.gnu.org/licenses/>.
 */

package de.meethub.util;

public class Pair<T1, T2> {

    private final T1 value1;
    private final T2 value2;

    public Pair(final T1 x, final T2 y) {
        this.value1 = x;
        this.value2 = y;
    }

    public static<X, Y> Pair<X, Y> create(final X x, final Y y) {
        return new Pair<X, Y>(x, y);
    }

    public T1 getFirst() {
        return this.value1;
    }

    public T2 getSecond() {
        return this.value2;
    }

}
