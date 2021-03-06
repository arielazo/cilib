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
package net.sourceforge.cilib.pso.crossover;

import java.util.List;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.entity.Particle;
import net.sourceforge.cilib.entity.operators.crossover.CrossoverStrategy;
import net.sourceforge.cilib.entity.operators.crossover.real.ParentCentricCrossoverStrategy;
import net.sourceforge.cilib.pso.crossover.pbestupdate.CurrentPositionOffspringPBestProvider;
import net.sourceforge.cilib.pso.crossover.pbestupdate.OffspringPBestProvider;
import net.sourceforge.cilib.pso.crossover.velocityprovider.IdentityOffspringVelocityProvider;
import net.sourceforge.cilib.pso.crossover.velocityprovider.OffspringVelocityProvider;
import net.sourceforge.cilib.util.selection.recipes.ElitistSelector;

/**
 * <p>
 * A composite crossover strategy used to perform crossover on particles. Since
 * particles contain extra information that individuals do not have e.g. velocity,
 * pbest, etc., it is necessary to set those for an offspring particle to be valid.
 * </p>
 * 
 * <p>
 * Particle crossover requires a crossover strategy. It is the callers job to pass 
 * the correct number parents for the chosen crossover strategy since the crossover 
 * strategies throw an error if the wrong number of parents is received. Once the
 * crossover is performed the offspring's velocity, pbest and best fitness need 
 * to be set. This is done using the OffspringVelocityProvider, OffspringPBestProvider 
 * and OffspringBestFitnessProviders respectively. Selection is done by the calling
 * class.
 * </p>
 */
public class ParticleCrossoverStrategy implements CrossoverStrategy {
    private OffspringVelocityProvider velocityProvider;
    private OffspringPBestProvider pbestProvider;
    private CrossoverStrategy crossoverStrategy;
    
    public ParticleCrossoverStrategy() {
        this(new ParentCentricCrossoverStrategy(), new CurrentPositionOffspringPBestProvider(), 
                new IdentityOffspringVelocityProvider());
    }
    
    public ParticleCrossoverStrategy(CrossoverStrategy strategy, OffspringPBestProvider pbestUpdate, 
            OffspringVelocityProvider velUpdate) {
        this.crossoverStrategy = strategy;
        this.pbestProvider = pbestUpdate;
        this.velocityProvider = velUpdate;
    }
    
    public ParticleCrossoverStrategy(ParticleCrossoverStrategy copy) {
        this.crossoverStrategy = copy.crossoverStrategy.getClone();
        this.pbestProvider = copy.pbestProvider;
        this.velocityProvider = copy.velocityProvider;
    }
    
    @Override
    public ParticleCrossoverStrategy getClone() {
        return new ParticleCrossoverStrategy(this);
    }
    
    @Override
    public <E extends Entity> List<E> crossover(List<E> parentCollection) {
        List<Particle> parents = (List<Particle>) parentCollection;
        List<Particle> offspring = crossoverStrategy.crossover(parents);
        Particle nBest = new ElitistSelector<Particle>().on(parents).select();
        
        for (Particle p : offspring) {
            p.getProperties().put(EntityType.Particle.BEST_POSITION, pbestProvider.f(parents, p));
            
            Particle pbCalc = p.getClone();
            pbCalc.setNeighbourhoodBest(nBest);
            pbCalc.setCandidateSolution(p.getBestPosition());
            pbCalc.calculateFitness();
            
            p.getProperties().put(EntityType.Particle.BEST_FITNESS, pbCalc.getFitness());
            p.getProperties().put(EntityType.Particle.VELOCITY, velocityProvider.f(parents, p));
            
            p.setNeighbourhoodBest(nBest);
            p.calculateFitness();
        }
        
        return (List<E>) offspring;
    }

    public void setVelocityProvider(OffspringVelocityProvider velocityProvider) {
        this.velocityProvider = velocityProvider;
    }

    public OffspringVelocityProvider getVelocityProvider() {
        return velocityProvider;
    }

    public void setPbestProvider(OffspringPBestProvider pbestProvider) {
        this.pbestProvider = pbestProvider;
    }

    public OffspringPBestProvider getPbestProvider() {
        return pbestProvider;
    }

    public void setCrossoverStrategy(CrossoverStrategy crossoverStrategy) {
        this.crossoverStrategy = crossoverStrategy;
    }

    public CrossoverStrategy getCrossoverStrategy() {
        return crossoverStrategy;
    }

    @Override
    public int getNumberOfParents() {
        return crossoverStrategy.getNumberOfParents();
    }
}
