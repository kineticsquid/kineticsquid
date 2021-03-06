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

import com.hp.hpl.jena.n3.IRIResolver ;
import com.hp.hpl.jena.shared.PrefixMapping ;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl ;

import org.openjena.atlas.lib.Lib ;
import org.openjena.atlas.logging.Log ;
import com.hp.hpl.jena.sparql.util.PrefixMapping2 ;

//** Prologues - combines with PrefixMapping (the RIOT Prologue uses PrefixMap) */
public class Prologue
{
    protected boolean seenBaseURI = false ;     // Implicit or set.
//    protected String baseURI = null ;

    protected PrefixMapping prefixMap = null ;
    protected IRIResolver resolver = null ;
    
    public Prologue() { prefixMap = new PrefixMappingImpl() ; }
    
    public Prologue(PrefixMapping pmap)
    { 
        this.prefixMap = pmap ; 
        this.resolver = null ;
    }
    
    public Prologue(PrefixMapping pmap, String base)
    { 
        this.prefixMap = pmap ;
        setBaseURI(base) ;
    }
    
    public Prologue(PrefixMapping pmap, IRIResolver resolver)
    {
        this.prefixMap = pmap ; 
        this.resolver = resolver ;
    }
    
    public Prologue(Prologue other)
    {
        this.prefixMap = other.prefixMap ; 
        this.resolver = other.resolver ;
    }

    public Prologue copy()
    {
        PrefixMapping prefixMap = new PrefixMappingImpl() ;
        prefixMap.setNsPrefixes(this.prefixMap) ;
        return new Prologue(prefixMap, resolver.getBaseIRI()) ;
    }
    
    // Reverse of sub()
    public void usePrologueFrom(Prologue other)
    {
        prefixMap = new PrefixMapping2(other.prefixMap) ;
        seenBaseURI = false ;
        if ( other.resolver != null )
            resolver = new IRIResolver(other.resolver.getBaseIRI()) ;
    }
    
    public Prologue sub(PrefixMapping newMappings) { return sub(newMappings, null) ; }
    public Prologue sub(String base) { return sub(null, base) ; }
    
    public Prologue sub(PrefixMapping newMappings, String base)
    {
        // New prefix mappings
        PrefixMapping ext = getPrefixMapping() ;
        if ( newMappings != null )
            ext = new PrefixMapping2(ext, newMappings) ;
        // New base.
        IRIResolver r = resolver ;
        if ( base != null )
            r = new IRIResolver(base) ;
        return new Prologue(ext, r) ;
    }
    
    /**
     * @return True if the query has an explicitly set base URI. 
     */
    public boolean explicitlySetBaseURI() { return seenBaseURI ; }

    /**
     * @return Returns the baseURI, if set.
     */
    public String getBaseURI()
    {
        if ( resolver == null )
            return null ;
        
//        if ( baseURI == null )
//            setDefaultBaseIRI() ;
        return resolver.getBaseIRI();
    }
    /**
     * @param baseURI The baseURI to set.
     */
    public void setBaseURI(String baseURI)
    {
        this.seenBaseURI = true ;
        this.resolver = new IRIResolver(baseURI) ; 
    }
    
    /**
     * @param resolver IRI resolver
     */
    public void setBaseURI(IRIResolver resolver)
    {
        this.seenBaseURI = true ;
        this.resolver = resolver ; 
    }
    
//    protected void setDefaultBaseIRI() { setDefaultBaseIRI(null) ; }
//    
//    protected void setDefaultBaseIRI(String base)
//    {
//        if ( baseURI != null )
//            return ;
//        
//        baseURI = IRIResolver.chooseBaseURI(base) ;
//    }
    
    // ---- Query prefixes
    
    /** Set a prefix for this query */
    public void setPrefix(String prefix, String expansion)
    {
        try {
            // Removal may involve regeneration of the reverse mapping
            // so only do if needed.   
            String oldExpansion = prefixMap.getNsPrefixURI(prefix) ;
            if ( Lib.equal(oldExpansion, expansion) )
                return ;
            if ( oldExpansion != null )
                prefixMap.removeNsPrefix(prefix) ;
            
            prefixMap.setNsPrefix(prefix, expansion) ;
        } catch (PrefixMapping.IllegalPrefixException ex)
        {
            Log.warn(this, "Illegal prefix mapping(ignored): "+prefix+"=>"+expansion) ;
        }
    }   

    /** Return the prefix map from the parsed query */ 
    public PrefixMapping getPrefixMapping() { return prefixMap ; }
    /** Set the mapping */
    public void setPrefixMapping(PrefixMapping pmap ) { prefixMap = pmap ; }

    /** Lookup a prefix for this query, including the default prefixes */
    public String getPrefix(String prefix)
    {
        return prefixMap.getNsPrefixURI(prefix) ;
    }

    /** Get the IRI resolver */
    public IRIResolver getResolver() { return resolver ; }
    
    /** Set the IRI resolver */
    public void setResolver(IRIResolver resolver) { this.resolver = resolver; }
    
    /** Expand prefixed name 
     * 
     * @param qname  The prefixed name to be expanded
     * @return URI, or null if not expanded.
     */

    public String expandPrefixedName(String qname)
    {
        String s = prefixMap.expandPrefix(qname) ;
        if ( s.equals(qname) )
            return null ;
        return s ;
    }
    
    /** Use the prefix map to turn a URI into a qname, or return the original URI */
    
    public String shortForm(String uri)
    {
        return prefixMap.shortForm(uri) ;
    }
}
