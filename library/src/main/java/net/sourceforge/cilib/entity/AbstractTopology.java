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
package net.sourceforge.cilib.entity;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.visitor.TopologyVisitor;

/**
 * This an abstract class which extends from the abstract Topology class.
 * All {@linkplain net.sourceforge.cilib.algorithm.population.PopulationBasedAlgorithm}
 * Topologies must inherit from this class.
 *
 * @param <E> The {@code Entity} type.
 */
public abstract class AbstractTopology<E extends Entity> extends ForwardingList<E> implements Topology<E> {
    private static final long serialVersionUID = -9117512234439769226L;
    
    protected List<E> entities;
    protected ControlParameter neighbourhoodSize;
    
    /**
     * Default constructor.
     */
    public AbstractTopology() {
        this.entities = Lists.<E>newLinkedList();
    }
    
    /**
     * Copy constructor.
     */
    public AbstractTopology(AbstractTopology<E> copy) {
        this.neighbourhoodSize = copy.neighbourhoodSize;
        this.entities = Lists.<E>newLinkedList();
        
        for (E entity : copy.entities) {
            this.entities.add((E) entity.getClone());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return new TopologyIterator<E>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(TopologyVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final AbstractTopology<E> other = (AbstractTopology<E>) obj;
        if (this.entities != other.entities && (this.entities == null || !this.entities.equals(other.entities))) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.entities != null ? this.entities.hashCode() : 0);
        return hash;
    }

    @Override
    protected List<E> delegate() {
        return this.entities;
    }

    /**
     * An iterator that iterates through the whole topology.
     * 
     * @param <T> The {@linkplain Entity} type.
     */
    protected class TopologyIterator<T extends Entity> implements IndexedIterator<T> {
        
        private int index;
        private AbstractTopology<T> topology;

        public TopologyIterator(AbstractTopology<T> topology) {
            this.topology = topology;
            this.index = -1;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public boolean hasNext() {
            int lastIndex = topology.entities.size() - 1;
            return (index != lastIndex) && (lastIndex >= 0);
        }

        @Override
        public T next() {
            int lastIndex = topology.entities.size() - 1;
            if (index == lastIndex) {
                throw new NoSuchElementException();
            }

            ++index;

            return topology.entities.get(index);
        }

        @Override
        public void remove() {
            if (index == -1) {
                throw new IllegalStateException();
            }

            topology.entities.remove(index);
            --index;
        }
    }
    
    /**
     * An iterator that iterates through a neighbourhood in the topology.
     * 
     * @param <T> The {@linkplain Entity} type.
     */
    protected abstract class NeighbourhoodIterator<T extends Entity> implements IndexedIterator<T> {
        protected int index;
        protected AbstractTopology<T> topology;
        
        public NeighbourhoodIterator(AbstractTopology<T> topology, IndexedIterator<T> iterator) {
            if (iterator.getIndex() == -1) {
                throw new IllegalStateException();
            }
            
            this.topology = topology;
        }
        
        @Override
        public int getIndex() {
            return index;
        }
    }
}
