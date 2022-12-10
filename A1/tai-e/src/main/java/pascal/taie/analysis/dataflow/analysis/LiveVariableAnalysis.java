/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.dataflow.analysis;

import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.exp.ExpVisitor;
import pascal.taie.ir.exp.RValue;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Stmt;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of classic live variable analysis.
 */
public class LiveVariableAnalysis extends
        AbstractDataflowAnalysis<Stmt, SetFact<Var>> {

    public static final String ID = "livevar";

    public LiveVariableAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public SetFact<Var> newBoundaryFact(CFG<Stmt> cfg) {
        // TODO - finish me
        // Stmt exit = cfg.getExit();
        return new SetFact<Var>();
    }

    @Override
    public SetFact<Var> newInitialFact() {
        // TODO - finish me
        return new SetFact<Var>();
    }

    @Override
    public void meetInto(SetFact<Var> fact, SetFact<Var> target) {
        // TODO - finish me
        if (target != null){
            target.union(fact);
        }
    }

    @Override
    public boolean transferNode(Stmt stmt, SetFact<Var> in, SetFact<Var> out) {
        // TODO - finish me
        // ExpVisitor<Var> visitor = null;
        SetFact<Var> def = new SetFact<Var>();
        Var left = null;
        if (stmt.getDef().isPresent()) {
            if (stmt.getDef().get() instanceof Var){
                left = (Var) stmt.getDef().get();
            }
            if (left != null) {
                def.add(left);
            }
        }

        SetFact<Var> used = new SetFact<Var>();
        List<RValue> rvalues = stmt.getUses();
        for (RValue r : rvalues){
            if (r instanceof Var) {
                used.add((Var) r);
            }
        }
        if (out != null) {
            var temp = out.copy();
            temp.remove(left);
            used.union(temp);
        }


        if (in.equals(used)){
            return false;
        }else{
            // in = used.copy();  //wrong! 主要是因为自己不是Java语言使用者
            in.set(used);
            return true;
        }
    }
}
