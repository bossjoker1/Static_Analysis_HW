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

package pascal.taie.analysis.dataflow.analysis.constprop;

import pascal.taie.analysis.dataflow.analysis.AbstractDataflowAnalysis;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.IR;
import pascal.taie.ir.exp.ArithmeticExp;
import pascal.taie.ir.exp.BinaryExp;
import pascal.taie.ir.exp.BitwiseExp;
import pascal.taie.ir.exp.ConditionExp;
import pascal.taie.ir.exp.Exp;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.exp.ShiftExp;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.DefinitionStmt;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.language.type.PrimitiveType;
import pascal.taie.language.type.Type;
import pascal.taie.util.AnalysisException;

public class ConstantPropagation extends
        AbstractDataflowAnalysis<Stmt, CPFact> {

    public static final String ID = "constprop";

    public ConstantPropagation(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return true;
    }

    @Override
    public CPFact newBoundaryFact(CFG<Stmt> cfg) {
        // TODO - finish me
        CPFact res = new CPFact();
        cfg.getIR().getParams().forEach(var -> {
            if (canHoldInt(var)) {
                res.update(var, Value.getNAC());
            }
        });
        return res;
    }

    @Override
    public CPFact newInitialFact() {
        // TODO - finish me
        return new CPFact();
    }

    @Override
    public void meetInto(CPFact fact, CPFact target) {
        // TODO - finish me
        fact.forEach((var, val) ->{
            target.update(var, meetValue(val, target.get(var)));
        });
    }

    /**
     * Meets two Values.
     */
    public Value meetValue(Value v1, Value v2) {
        // TODO - finish me
        // follow the slides p238
        if (v1.isNAC() || v2.isNAC()) {
            return Value.getNAC();
        }
        if (v1.isUndef() && v2.isConstant()) {
            return Value.makeConstant(v2.getConstant());
        }
        if (v2.isUndef() && v1.isConstant()) {
            return Value.makeConstant(v1.getConstant());
        }
        if (v1.isConstant() && v2.isConstant()) {
            if (v1.equals(v2)) {
                return Value.makeConstant(v1.getConstant());
            } else {
                return Value.getNAC();
            }
        }

        return Value.getUndef();
    }

    @Override
    public boolean transferNode(Stmt stmt, CPFact in, CPFact out) {
        // TODO - finish me
        boolean flag = false;
        CPFact temp = in.copy();

        if(stmt instanceof DefinitionStmt<?, ?> s){
            // kill and gen
            if(s.getLValue() instanceof Var var && canHoldInt(var)){
                temp.remove(var);
                Value gen = evaluate(s.getRValue(), in);
                temp.update(var, gen);
            }
            if(!out.equals(temp)){
                temp.forEach(out::update);
//                temp.forEach((var, val) ->{
//                    out.update(var, val);
//                });
                flag = true;
            }
        }else{
            if(!out.equals(in)){
                in.forEach((out::update));
                flag = true;
            }
        }
        return flag;
    }

    /**
     * @return true if the given variable can hold integer value, otherwise false.
     */
    public static boolean canHoldInt(Var var) {
        Type type = var.getType();
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BYTE:
                case SHORT:
                case INT:
                case CHAR:
                case BOOLEAN:
                    return true;
            }
        }
        return false;
    }

    /**
     * Evaluates the {@link Value} of given expression.
     *
     * @param exp the expression to be evaluated
     * @param in  IN fact of the statement
     * @return the resulting {@link Value}
     */
    public static Value evaluate(Exp exp, CPFact in) {
        // TODO - finish me
        // follow the slides p247
        // here is just one expression, so we just get with 0 idx.
        if (exp instanceof IntLiteral e) {
            return Value.makeConstant(e.getValue());
        } else if (exp instanceof Var v) {
            return in.get(v);
        } else if (exp instanceof BinaryExp bi) {
            Value a = in.get(bi.getOperand1());
            Value b = in.get(bi.getOperand2());
            // to pass the special testcase
            if (a.isNAC() && b.isConstant()){
                int i2 = b.getConstant();
                if (bi.getOperator() instanceof ArithmeticExp.Op op) {
                    // hint: special case
                    if ((op == ArithmeticExp.Op.DIV || op == ArithmeticExp.Op.REM) && i2 == 0) {
                        return Value.getUndef();
                    }
                }
            }
            if (a.isConstant() && b.isConstant()) {
                int i1 = a.getConstant(), i2 = b.getConstant();
                if (bi.getOperator() instanceof ArithmeticExp.Op op) {
                    // hint: special case
                    if ((op == ArithmeticExp.Op.DIV || op == ArithmeticExp.Op.REM) && i2 == 0) {
                        return Value.getUndef();
                    }
                    switch (op) {
                        case ADD -> {
                            return Value.makeConstant(i1 + i2);
                        }
                        case SUB -> {
                            return Value.makeConstant(i1 - i2);
                        }
                        case DIV -> {
                            return Value.makeConstant(i1 / i2);
                        }
                        case MUL -> {
                            return Value.makeConstant(i1 * i2);
                        }
                        case REM -> {
                            return Value.makeConstant(i1 % i2);
                        }
                    }
                } else if (bi.getOperator() instanceof BitwiseExp.Op op) {
                    switch (op) {
                        case OR -> {
                            return Value.makeConstant(i1 | i2);
                        }
                        case AND -> {
                            return Value.makeConstant(i1 & i2);
                        }
                        case XOR -> {
                            return Value.makeConstant(i1 ^ i2);
                        }
                    }
                } else if (bi.getOperator() instanceof ShiftExp.Op op) {
                    switch (op) {
                        case SHL -> {
                            return Value.makeConstant(i1 << i2);
                        }
                        case SHR -> {
                            return Value.makeConstant(i1 >> i2);
                        }
                        case USHR -> {
                            return Value.makeConstant(i1 >>> i2);
                        }
                    }
                } else if (bi.getOperator() instanceof ConditionExp.Op op) {
                    // 1 for true, o for false
                    switch (op) {
                        case EQ -> {
                            return Value.makeConstant(i1 == i2 ? 1 : 0);
                        }
                        case NE -> {
                            return Value.makeConstant(i1 != i2 ? 1 : 0);
                        }
                        case LT -> {
                            return Value.makeConstant(i1 < i2 ? 1 : 0);
                        }
                        case GT -> {
                            return Value.makeConstant(i1 > i2 ? 1 : 0);
                        }
                        case LE -> {
                            return Value.makeConstant(i1 <= i2 ? 1 : 0);
                        }
                        case GE -> {
                            return Value.makeConstant(i1 >= i2 ? 1 : 0);
                        }
                    }
                }
            }else if(a.isNAC() || b.isNAC()){
                return Value.getNAC();
            }else{
                return Value.getUndef();
            }
        }
        return Value.getNAC();
    }
}
