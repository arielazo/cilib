/**
 * Computational Intelligence Library (CIlib)
 * Copyright (C) 2003 - 2010
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.cilib.entity.comparator;

import java.io.Serializable;
import java.util.Comparator;
import net.sourceforge.cilib.entity.SocialEntity;
import net.sourceforge.cilib.problem.solution.Fitness;

/**
 * Compare two {@link SocialEntity} instances, based on the available social best
 * fitness.
 * @param <E> The {@code SocialEntity} type.
 * @see SocialEntity#getSocialBestFitness()
 */
public class SocialBestFitnessComparator<E extends SocialEntity> implements Comparator<E>, Serializable {
    private static final long serialVersionUID = 9035734190908368266L;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(E o1, E o2) {
        Fitness f1 = o1.getSocialFitness();
        Fitness f2 = o2.getSocialFitness();

        return f1.compareTo(f2);
    }

}