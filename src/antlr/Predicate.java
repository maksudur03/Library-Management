package antlr;

public static class Predicate extends SemanticContext {
    /** The AST node in tree created from the grammar holding the predicate */
    public GrammarAST predicateAST;

    /** Is this a {...}?=> gating predicate or a normal disambiguating {..}?
     *  If any predicate in expression is gated, then expression is considered
     *  gated.
     *
     *  The simple Predicate object's predicate AST's type is used to set
     *  gated to true if type==GATED_SEMPRED.
     */
    protected boolean gated = false;

    /** syntactic predicates are converted to semantic predicates
     *  but synpreds are generated slightly differently.
     */
    protected boolean synpred = false;

    public static final int INVALID_PRED_VALUE = -2;
    public static final int FALSE_PRED = 0;
    public static final int TRUE_PRED = ~0;

    /** sometimes predicates are known to be true or false; we need
     *  a way to represent this without resorting to a target language
     *  value like true or TRUE.
     */
    protected int constantValue = INVALID_PRED_VALUE;

    public Predicate(int constantValue) {
        predicateAST = new GrammarAST();
        this.constantValue=constantValue;
    }

    public Predicate(GrammarAST predicate) {
        this.predicateAST = predicate;
        this.gated =
                predicate.getType()==ANTLRParser.GATED_SEMPRED ||
                        predicate.getType()==ANTLRParser.SYN_SEMPRED ;
        this.synpred =
                predicate.getType()==ANTLRParser.SYN_SEMPRED ||
                        predicate.getType()==ANTLRParser.BACKTRACK_SEMPRED;
    }

    public Predicate(Predicate p) {
        this.predicateAST = p.predicateAST;
        this.gated = p.gated;
        this.synpred = p.synpred;
        this.constantValue = p.constantValue;
    }

    /** Two predicates are the same if they are literally the same
     *  text rather than same node in the grammar's AST.
     *  Or, if they have the same constant value, return equal.
     *  As of July 2006 I'm not sure these are needed.
     */
    public boolean equals(Object o) {
        if ( !(o instanceof Predicate) ) {
            return false;
        }

        Predicate other = (Predicate)o;
        if (this.constantValue != other.constantValue){
            return false;
        }

        if (this.constantValue != INVALID_PRED_VALUE){
            return true;
        }

        return predicateAST.getText().equals(other.predicateAST.getText());
    }

    public int hashCode() {
        if (constantValue != INVALID_PRED_VALUE){
            return constantValue;
        }

        if ( predicateAST ==null ) {
            return 0;
        }

        return predicateAST.getText().hashCode();
    }

    public ST genExpr(CodeGenerator generator,
                      STGroup templates,
                      antlr.DFA.analysis.DFA dfa)
    {
        ST eST = null;
        if ( templates!=null ) {
            if ( synpred ) {
                eST = templates.getInstanceOf("evalSynPredicate");
            }
            else {
                eST = templates.getInstanceOf("evalPredicate");
                generator.grammar.decisionsWhoseDFAsUsesSemPreds.add(dfa);
            }
            String predEnclosingRuleName = predicateAST.enclosingRuleName;
				/*
				String decisionEnclosingRuleName =
					dfa.getNFADecisionStartState().getEnclosingRule();
				// if these rulenames are diff, then pred was hoisted out of rule
				// Currently I don't warn you about this as it could be annoying.
				// I do the translation anyway.
				*/
            //eST.add("pred", this.toString());
            if ( generator!=null ) {
                eST.add("pred",
                        generator.translateAction(predEnclosingRuleName,predicateAST));
            }
        }
        else {
            eST = new ST("<pred>");
            eST.add("pred", this.toString());
            return eST;
        }
        if ( generator!=null ) {
            String description =
                    generator.target.getTargetStringLiteralFromString(this.toString());
            eST.add("description", description);
        }
        return eST;
    }

    @Override
    public SemanticContext getGatedPredicateContext() {
        if ( gated ) {
            return this;
        }
        return null;
    }

    @Override
    public boolean hasUserSemanticPredicate() { // user-specified sempred
        return predicateAST !=null &&
                ( predicateAST.getType()==ANTLRParser.GATED_SEMPRED ||
                        predicateAST.getType()==ANTLRParser.SEMPRED );
    }

    @Override
    public boolean isSyntacticPredicate() {
        return predicateAST !=null &&
                ( predicateAST.getType()==ANTLRParser.SYN_SEMPRED ||
                        predicateAST.getType()==ANTLRParser.BACKTRACK_SEMPRED );
    }

    @Override
    public void trackUseOfSyntacticPredicates(Grammar g) {
        if ( synpred ) {
            g.synPredNamesUsedInDFA.add(predicateAST.getText());
        }
    }

    @Override
    public String toString() {
        if ( predicateAST ==null ) {
            return "<nopred>";
        }
        return predicateAST.getText();
    }
}
