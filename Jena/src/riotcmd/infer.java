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

package riotcmd;

import java.io.InputStream ;
import java.util.List ;

import org.openjena.atlas.io.IO ;
import org.openjena.atlas.lib.IRILib ;
import org.openjena.atlas.lib.Sink ;
import org.openjena.riot.Lang ;
import org.openjena.riot.RiotReader ;
import org.openjena.riot.lang.LangRIOT ;
import org.openjena.riot.out.SinkQuadOutput ;
import org.openjena.riot.pipeline.inf.InfFactory ;
import org.openjena.riot.system.SinkExtendTriplesToQuads ;
import arq.cmd.CmdException ;
import arq.cmdline.ArgDecl ;
import arq.cmdline.CmdGeneral ;

import com.hp.hpl.jena.graph.Triple ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.sparql.core.Quad ;
import com.hp.hpl.jena.util.FileManager ;

/*
 * TDB Infer
 *   RDFS
 *   owl:sameAs (in T-Box, not A-Box)
 *   owl:equivalentClass, owl:equivalentProperty
 *   owl:TransitiveProperty, owl:SymmetricProperty
 *
 * OWLprime - Oracle
- rdfs:domain
- rdfs:range
- rdfs:subClassOf
- rdfs:subPropertyOf
- owl:equivalentClass
- owl:equivalentProperty
- owl:sameAs
- owl:inverseOf
- owl:TransitiveProperty
- owl:SymmetricProperty
- owl:FunctionalProperty
- owl:InverseFunctionalProperty

 JimH: RDFS3:
 #
    * equivalentClass
    * equivalentProperty
    * sameAs
    * differentFrom (and allDifferent) 

# Property Characteristics:

    * inverseOf
    * TransitiveProperty
    * SymmetricProperty
    * FunctionalProperty
    * InverseFunctionalProperty
    * ObjectProperty
    * DatatypeProperty
    * disjointWith 

AllegroGraph RDFS++
    * rdf:type
    * rdfs:subClassOf
    * rdfs:domain and rdfs:range
    * rdfs:subPropertyOf
    * owl:sameAs
    * owl:inverseOf
    * owl:TransitiveProperty
 */
public class infer extends CmdGeneral
{
    static final ArgDecl argRDFS = new ArgDecl(ArgDecl.HasValue, "rdfs") ;
    private Model vocab ;
    
    public static void main(String... argv)
    {
        new infer(argv).mainRun() ;
    }        

    protected infer(String[] argv)
    {
        super(argv) ;
        super.add(argRDFS) ;
    }

//    public static void expand(String filename, Model vocab)
//    {
//        Sink<Triple> sink = new SinkTripleOutput(System.out) ;
//        sink = new InferenceExpanderRDFS(sink, vocab) ;
//        RiotReader.parseTriples(filename, sink) ;
//        IO.flush(System.out); 
//    }

    @Override
    protected String getSummary()
    {
        return "infer --rdfs=vocab FILE ..." ;
    }

    @Override
    protected void processModulesAndArgs()
    {
        if ( ! contains(argRDFS) )
            throw new CmdException("Required argument missing: --"+argRDFS.getKeyName()) ;
        String fn = getValue(argRDFS) ;
        vocab = FileManager.get().loadModel(fn) ;
    }

    @Override
    protected void exec()
    {
        Sink<Quad> sink = new SinkQuadOutput(System.out) ;
        sink = InfFactory.infQuads(sink, vocab) ;
        
        List<String> files = getPositionalOrStdin() ;
        if ( files.isEmpty() )
            files.add("-") ;
            
        for ( String fn : files )
            processFile(fn, sink) ;
        IO.flush(System.out); 
    }

    private void processFile(String filename, Sink<Quad> sink)
    {
        Lang lang = filename.equals("-") ? Lang.NQUADS : Lang.guess(filename, Lang.NQUADS) ;
        String baseURI = IRILib.filenameToIRI(filename) ;
        
        if ( lang.isTriples() )
        {
            InputStream in = IO.openFile(filename) ;
            Sink<Triple> sink2 = new SinkExtendTriplesToQuads(sink) ;
            LangRIOT parser = RiotReader.createParserTriples(in, lang, baseURI, sink2) ;
            parser.parse() ;
            return ;
        }
        else
        {
            InputStream in = IO.openFile(filename) ;
            LangRIOT parser = RiotReader.createParserQuads(in, lang, baseURI, sink) ; 
            parser.parse() ;
        }        
    }

    @Override
    protected String getCommandName()
    {
        return "infer" ;
    }
}
