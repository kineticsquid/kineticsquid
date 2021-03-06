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

package com.hp.hpl.jena.sparql.algebra;

import java.util.List ;

import com.hp.hpl.jena.query.ResultSet ;
import com.hp.hpl.jena.sparql.core.Var ;
import com.hp.hpl.jena.sparql.engine.ExecutionContext ;
import com.hp.hpl.jena.sparql.engine.QueryIterator ;
import com.hp.hpl.jena.sparql.engine.binding.Binding ;
import com.hp.hpl.jena.sparql.expr.ExprList ;

public interface Table
{
    // Note - this table is the RIGHT table, and takes a LEFT binding.
    public QueryIterator matchRightLeft(Binding bindingLeft, 
                                        boolean includeOnNoMatch,
                                        ExprList condition,
                                        ExecutionContext execCxt) ;

    public void close() ;
    public List<Var> getVars() ;
    public List<String> getVarNames() ;
    public int size() ;
    public boolean isEmpty() ;
    public QueryIterator iterator(ExecutionContext execCxt) ;
    public void addBinding(Binding binding) ;
    public boolean contains(Binding binding) ;
    public ResultSet toResultSet() ;
}
