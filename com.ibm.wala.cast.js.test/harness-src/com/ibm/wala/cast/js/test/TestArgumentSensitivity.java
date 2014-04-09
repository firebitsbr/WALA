/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.cast.js.test;

import java.io.IOException;

import org.junit.Test;

import com.ibm.wala.cast.ipa.callgraph.CAstCallGraphUtil;
import com.ibm.wala.cast.js.ipa.callgraph.ArgumentSpecialization;
import com.ibm.wala.cast.js.ipa.callgraph.JSAnalysisOptions;
import com.ibm.wala.cast.js.ipa.callgraph.JSCFABuilder;
import com.ibm.wala.cast.js.ipa.callgraph.JSZeroOrOneXCFABuilder;
import com.ibm.wala.cast.js.loader.JavaScriptLoaderFactory;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

public abstract class TestArgumentSensitivity extends TestJSCallGraphShape {

  protected static final Object[][] assertionsForArgs = new Object[][] {
    new Object[] { ROOT, new String[] { "tests/args.js" } },
    new Object[] {
        "tests/args.js",
        new String[] { "tests/args.js/a" } },
    new Object[] { "tests/args.js/a", new String[] { "tests/args.js/x"} },
    new Object[] { "tests/args.js/a", new String[] { "tests/args.js/y", "tests/args.js/z", "!tests/args.js/wrong" } } };

  @Test public void testArgs() throws IOException, IllegalArgumentException, CancelException, ClassHierarchyException, WalaException {
    JavaScriptLoaderFactory loaders = JSCallGraphBuilderUtil.makeLoaders(null);
    AnalysisScope scope = JSCallGraphBuilderUtil.makeScriptScope("tests", "args.js", loaders);

    IClassHierarchy cha = JSCallGraphBuilderUtil.makeHierarchy(scope, loaders);
    com.ibm.wala.cast.js.util.Util.checkForFrontEndErrors(cha);
    Iterable<Entrypoint> roots = JSCallGraphBuilderUtil.makeScriptRoots(cha);
    JSAnalysisOptions options = JSCallGraphBuilderUtil.makeOptions(scope, cha, roots);

    AnalysisCache cache = JSCallGraphBuilderUtil.makeCache(new ArgumentSpecialization.ArgumentCountIRFactory(options.getSSAOptions()));

    JSCFABuilder builder = new JSZeroOrOneXCFABuilder(cha, options, cache, null, null, ZeroXInstanceKeys.ALLOCATIONS, false);
    builder.setContextSelector(new ArgumentSpecialization.ArgumentCountContextSelector(builder.getContextSelector()));
    builder.setContextInterpreter(new ArgumentSpecialization.ArgumentSpecializationContextIntepreter(options, cache));
    CallGraph CG = builder.makeCallGraph(options);

//    CAstCallGraphUtil.AVOID_DUMP = false;
    CAstCallGraphUtil.dumpCG(builder.getPointerAnalysis(), CG);
    
    verifyGraphAssertions(CG, assertionsForArgs);
  }

}
