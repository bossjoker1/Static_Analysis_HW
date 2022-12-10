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

package pascal.taie.analysis.dataflow.solver;

import pascal.taie.analysis.dataflow.analysis.DataflowAnalysis;
import pascal.taie.analysis.dataflow.fact.DataflowResult;
import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.ir.exp.Var;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

class IterativeSolver<Node, Fact> extends Solver<Node, Fact> {

    public IterativeSolver(DataflowAnalysis<Node, Fact> analysis) {
        super(analysis);
    }

    @Override
    protected void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me
        boolean flag = true;
        while (flag){
            // per iteration
            // we should get Exit node first due to the backward direction
            flag = false;
//            Node exit = cfg.getExit();
//            Queue<Node> q = new LinkedList<Node>();
//            Set<Node> s = new HashSet<Node>();
//            q.offer(exit);
//            s.add(exit);
//            while (!q.isEmpty()){
//                Node cur = q.poll();
//                for (Node node : cfg.getPredsOf(cur)){
//                    if (cfg.isEntry(node) || s.contains(node)){
//                        break;
//                    }
//                    s.add(node);
//                    q.offer(node);
//                    // begin to search current node's successors
//                    for(Node suc : cfg.getSuccsOf(node)){
//                        analysis.meetInto(result.getInFact(suc), result.getOutFact(node));
//                    }
//                    flag |= analysis.transferNode(node, result.getInFact(node), result.getOutFact(node));
//                }
//            }
            for (Node node : cfg){
                if (cfg.isExit(node)) continue;
                for (Node suc : cfg.getSuccsOf(node)){
                    analysis.meetInto(result.getInFact(suc), result.getOutFact(node));
                }
                flag |= analysis.transferNode(node, result.getInFact(node), result.getOutFact(node));
            }
        }
    }
}
