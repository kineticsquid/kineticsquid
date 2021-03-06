/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.sparql.core;

import java.util.ArrayList ;
import java.util.List ;
import java.util.ListIterator ;

import org.openjena.atlas.io.IndentedLineBuffer ;

import com.hp.hpl.jena.sparql.serializer.SerializationContext ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.sparql.sse.writers.WriterNode ;


/** A class whose purpose is to give a name to a collection of quads
 */ 

public class QuadPattern implements Iterable<Quad>
{
    private List<Quad> quads = new ArrayList<Quad>() ; 

    public QuadPattern() {}
    public QuadPattern(QuadPattern other) {quads.addAll(other.quads) ; }
    
    public void add(Quad q) { quads.add(q) ; }
    public void addAll(QuadPattern other) { quads.addAll(other.quads) ; }
    public void add(int i, Quad q) { quads.add(i, q) ; }
    
    public Quad get(int i) { return quads.get(i) ; }
    @Override
    public ListIterator<Quad> iterator() { return quads.listIterator() ; } 
    public int size() { return quads.size() ; }
    public boolean isEmpty() { return quads.isEmpty() ; }
    
    public List<Quad> getList() { return quads ; } 
    
    @Override
    public int hashCode() { return quads.hashCode() ; } 
    @Override
    public boolean equals(Object other)
    { 
        if ( this == other ) return true ;
        if ( ! ( other instanceof QuadPattern) ) 
            return false ;
        QuadPattern bp = (QuadPattern)other ;
        return quads.equals(bp.quads) ;
    }
    
    @Override
    public String toString()
    { 
        IndentedLineBuffer out = new IndentedLineBuffer() ;
        
        SerializationContext sCxt = SSE.sCxt((SSE.defaultPrefixMapWrite)) ;
        
        boolean first = true ;
        for ( Quad quad : quads )
        {
            if ( !first )
                out.print(" ") ;
            else
                first = false ;
            // Adds (triple ...)
            // SSE.write(buff.getIndentedWriter(), t) ;
            out.print("(") ;
            WriterNode.outputPlain(out, quad, sCxt) ;
            out.print(")") ;
        }
        out.flush();
        return out.toString() ;
    }
}
