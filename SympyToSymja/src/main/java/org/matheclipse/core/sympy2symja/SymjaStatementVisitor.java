package org.matheclipse.core.sympy2symja;

import java.util.List;
import java.util.Optional;
import io.github.danielnaczo.python3parser.model.Identifier;
import io.github.danielnaczo.python3parser.model.expr.Expression;
import io.github.danielnaczo.python3parser.model.expr.operators.Operator;
import io.github.danielnaczo.python3parser.model.stmts.Body;
import io.github.danielnaczo.python3parser.model.stmts.Statement;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.ClassDef;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.If;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.While;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.forStmts.AsyncFor;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.forStmts.For;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.functionStmts.AsyncFunctionDef;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.functionStmts.Decorator;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.functionStmts.FunctionDef;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.functionStmts.parameters.Parameters;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.tryExceptStmts.ExceptHandler;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.tryExceptStmts.Try;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.withStmts.AsyncWith;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.withStmts.With;
import io.github.danielnaczo.python3parser.model.stmts.compoundStmts.withStmts.WithItem;
import io.github.danielnaczo.python3parser.model.stmts.flowStmts.Break;
import io.github.danielnaczo.python3parser.model.stmts.flowStmts.Continue;
import io.github.danielnaczo.python3parser.model.stmts.flowStmts.Raise;
import io.github.danielnaczo.python3parser.model.stmts.flowStmts.Return;
import io.github.danielnaczo.python3parser.model.stmts.importStmts.Alias;
import io.github.danielnaczo.python3parser.model.stmts.importStmts.Import;
import io.github.danielnaczo.python3parser.model.stmts.importStmts.ImportFrom;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.Assert;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.Delete;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.Global;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.Nonlocal;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.Pass;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.assignStmts.AnnAssign;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.assignStmts.Assign;
import io.github.danielnaczo.python3parser.model.stmts.smallStmts.assignStmts.AugAssign;
import io.github.danielnaczo.python3parser.visitors.exceptions.Python3ParserException;

public class SymjaStatementVisitor extends SymjaGenericUnsupportedASTVisitor<String, SymjaIndentation> {

	@Override
	public String visitAnnAssign(AnnAssign annAssign, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());

		Expression target = annAssign.getTarget();
		Expression annotation = annAssign.getAnnotation();
		Optional<Expression> value = annAssign.getValue();

        string = string.concat(target.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat(": ");
        string = string.concat(annotation.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));

		if (value.isPresent()) {
			string = string.concat(" = ");
            string = string.concat(value.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
		}

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitAssert(Assert assertElement, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("assert ");

		Expression test = assertElement.getTest();
		Optional<Expression> msg = assertElement.getMsg();

        string = string.concat(test.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));

		if (msg.isPresent()) {
          string = string.concat(", " + msg.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
		}

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitAssign(Assign assign, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());

		List<Expression> targets = assign.getTargets();
		Optional<Expression> value = assign.getValue();
		
		if (targets == null || targets.isEmpty()) {
			throw new Python3ParserException("'Assign' has no targets.");
		}
		
		for (int i = 0; i < targets.size(); i++) {
          string = string.concat(targets.get(i).accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));

			if (i != targets.size() - 1) {
				string = string.concat(" = ");
			}
		}

		if (value.isPresent()) {
			string = string.concat(" = ");
            string = string.concat(value.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
		}

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitAsyncFor(AsyncFor asyncFor, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("async for ");

		Expression target = asyncFor.getTarget();
		Expression iter = asyncFor.getIter();
		Statement body = asyncFor.getBody();
		Optional<Statement> orElse = asyncFor.getOrElse();

        string = string.concat(target.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat(" in ");
        string = string.concat(iter.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));

		string = string.concat(":");
		string = string.concat("\n");

		string = bodyToString(param, string, body);

		if (orElse.isPresent()) {
			string = string.concat(param.getIndentationString());
			string = string.concat("else:");
			string = string.concat("\n");
			string = bodyToString(param, string, orElse.get());
		}

		return string;
	}

	@Override
	public String visitAsyncFunctionDef(AsyncFunctionDef asyncFunctionDef, SymjaIndentation param) {
		String string = new String();
		string = string.concat("\n");
		string = string.concat("\n");

		String name = asyncFunctionDef.getName().getName();
		Optional<Parameters> args = asyncFunctionDef.getParameters();
		Statement body = asyncFunctionDef.getBody();
		List<Decorator> decoratorList = asyncFunctionDef.getDecoratorList();
		Optional<Expression> returns = asyncFunctionDef.getReturns();

		for (int i = 0; i < decoratorList.size(); i++) {
			string = string.concat(param.getIndentationString());
			string = string.concat(decoratorList.get(i).accept(new SymjaDecoratorVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
			string = string.concat("\n");
		}

		string = string.concat(param.getIndentationString());
		string = string.concat("async def ");

		string = string.concat(name);

		string = string.concat("(");
		if (args.isPresent()) {
			string = string.concat(args.get().accept(new SymjaParametersVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
		}
		string = string.concat(")");

		if (returns.isPresent()) {
			string = string.concat(" -> ");
            string = string.concat(returns.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
			string = string.concat(" ");
		}

		string = string.concat(":");
		string = string.concat("\n");

		string = bodyToString(param, string, body);

		return string;
	}

	@Override
	public String visitAsyncWith(AsyncWith asyncWith, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());

		List<WithItem> items = asyncWith.getItems();
		Statement body = asyncWith.getBody();

		string = string.concat("async with ");
		if (items == null || items.isEmpty()) {
			throw new Python3ParserException("'AsyncWith' has no withItems.");
		}

		for (int i = 0; i < items.size(); i++) {
			string = string.concat(items.get(i).accept(new SymjaWithItemVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));

			if (i != items.size() - 1) {
				string = string.concat(", ");
			}
		}

		string = string.concat(":");
		string = string.concat("\n");
		if (body == null) {
			throw new Python3ParserException("Body of 'AsyncWith' is empty.");
		}
		string = bodyToString(param, string, body);
		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitAugAssign(AugAssign augAssign, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());

		Expression target = augAssign.getTarget();
		Operator op = augAssign.getOp();
		Expression value = augAssign.getValue();

        string = string.concat(target.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat(" ");
		string = string.concat(
            op.accept(new SymjaOperatorVisitor(),
                new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat("= ");
        string = string.concat(value.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitBody(Body body, SymjaIndentation param) {
		String string = new String();
		List<Statement> statements = body.getStatements();

		if (statements == null || statements.isEmpty()) {
			Statement parentStmt = body.getParentStmt();

			throw new Python3ParserException("Body of " + parentStmt.toString() + " is empty.");
		}

		for (int i = 0; i < statements.size(); i++) {
			Statement statement = statements.get(i);
			if (statement instanceof Expression) {
				string = string.concat(param.getIndentationString());
                string = string.concat(statement.accept(new SymjaExpressionVisitor(),
						new SymjaIndentation(param.getIndentationLevel())));
				string = string.concat("\n");
			} else {
				string = string.concat(statement.accept(new SymjaStatementVisitor(),
						new SymjaIndentation(param.getIndentationLevel())));
			}
		}

		return string;
	}

	@Override
	public String visitBreak(Break breakElement, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("break");
		string = string.concat("\n");
		return string;
	}

	@Override
	public String visitClassDef(ClassDef classDef, SymjaIndentation param) {
		String string = new String();
		string = string.concat("\n");
		string = string.concat("\n");

		String name = classDef.getName().getName();
		Optional<Expression> arguments = classDef.getArguments();
		Statement body = classDef.getBody();
		List<Decorator> decoratorList = classDef.getDecoratorList();

		for (int i = 0; i < decoratorList.size(); i++) {
			string = string.concat(param.getIndentationString());
			string = string.concat(decoratorList.get(i).accept(new SymjaDecoratorVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
			string = string.concat("\n");
		}

		string = string.concat(param.getIndentationString());
		string = string.concat("class ");
		string = string.concat(name);
		if (arguments.isPresent()) {
          string = string.concat(arguments.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
		}
		string = string.concat(":");
		string = string.concat("\n");
		string = string.concat("\n");

		if (body == null)
			throw new Python3ParserException("Body of " + classDef.toString() + " is empty.");

		string = bodyToString(param, string, body);

		return string;
	}

	@Override
	public String visitContinue(Continue continueElements, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("continue");
		string = string.concat("\n");
		return string;
	}

	@Override
	public String visitDelete(Delete delete, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("del ");
		Expression expression = delete.getExpression();
        string = string.concat(expression.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat("\n");
		return string;
	}

	@Override
	public String visitFor(For forElement, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("for ");

		Expression target = forElement.getTarget();
		Expression iter = forElement.getIter();
		Statement body = forElement.getBody();
		Optional<Statement> orElse = forElement.getOrElse();

		if (target == null) {
			throw new Python3ParserException("'For' has no 'target' value.");
		}
		if (iter == null) {
			throw new Python3ParserException("'For' has no 'iter' value.");
		}
		if (body == null) {
			throw new Python3ParserException("Body of 'For' is empty.");
		}

        string = string.concat(target.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat(" in ");
        string = string.concat(iter.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));

		string = string.concat(":");
		string = string.concat("\n");

		string = bodyToString(param, string, body);

		if (orElse.isPresent()) {
			string = string.concat(param.getIndentationString());
			string = string.concat("else:");
			string = string.concat("\n");
			string = bodyToString(param, string, orElse.get());
		}

		return string;
	}

	@Override
	public String visitFunctionDef(FunctionDef functionDef, SymjaIndentation param) {
		String string = new String();
		string = string.concat("\n");
		string = string.concat("\n");

		String name = functionDef.getName().getName();
		Optional<Parameters> parameters = functionDef.getParameters();
		Statement body = functionDef.getBody();
		List<Decorator> decoratorList = functionDef.getDecoratorList();
		Optional<Expression> returns = functionDef.getReturns();

		if (decoratorList != null) {
			for (int i = 0; i < decoratorList.size(); i++) {
				string = string.concat(param.getIndentationString());
				string = string.concat(decoratorList.get(i).accept(new SymjaDecoratorVisitor(),
						new SymjaIndentation(param.getIndentationLevel())));
				string = string.concat("\n");
			}
		}

		string = string.concat(param.getIndentationString());
		string = string.concat("def ");

		string = string.concat(name);

		string = string.concat("(");
		if (parameters.isPresent()) {
			string = string.concat(parameters.get().accept(new SymjaParametersVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
		}
		string = string.concat(")");

		if (returns.isPresent()) {
			string = string.concat(" -> ");
            string = string.concat(returns.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
			string = string.concat(" ");
		}

		string = string.concat(":");
		string = string.concat("\n");

		if (body == null)
			throw new Python3ParserException("Body of " + functionDef.toString() + " is empty.");
		
		string = bodyToString(param, string, body);

		return string;
	}

	@Override
	public String visitGlobal(Global global, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("global ");

		List<Identifier> names = global.getNames();
		if (names == null || names.isEmpty()) {
			throw new Python3ParserException("'Global' has no names.");
		}

		for (int i = 0; i < names.size(); i++) {
			string = string.concat(names.get(i).getName());
			if (i != names.size() - 1) {
				string = string.concat(", ");
			}
		}

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitIf(If ifElement, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());

		Expression ifTest = ifElement.getIfTest();
		Statement ifBody = ifElement.getIfBody();
		List<Expression> elifTests = ifElement.getElifTests();
		List<Statement> elifBodies = ifElement.getElifBodies();
		Optional<Statement> elseBody = ifElement.getElseBody();

		string = string.concat("if ");
		if (ifTest == null) {
			throw new Python3ParserException("Body of 'If' is empty.");
		}
        string = string.concat(ifTest.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat(":");

		if (ifBody instanceof Body) {
			string = string.concat("\n"); // ifSuite contains multiple statements
		} else {
			string = string.concat(" "); // ifSuite contains a simple statement
		}
		
		if (ifBody instanceof Expression) {
          string = string.concat(ifBody.accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel() + 1)));
			string = string.concat("\n");
		} else {
			string = string.concat(ifBody.accept(new SymjaStatementVisitor(),
					new SymjaIndentation(param.getIndentationLevel() + 1)));
		}

		for (int i = 0; i < elifTests.size(); i++) {
			string = string.concat(param.getIndentationString());
			string = string.concat("elif ");
            string = string.concat(elifTests.get(i).accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
			string = string.concat(":");
			string = string.concat("\n");
			if (elifBodies.get(i) == null) {
				throw new Python3ParserException("Body of handler is empty.");
			}
			string = bodyToString(param, string, elifBodies.get(i));
		}

		if (elseBody.isPresent()) {
			string = string.concat(param.getIndentationString());
			string = string.concat("else: ");
			if (elseBody.get() instanceof Body) {
				string = string.concat("\n"); // elseBody contains multiple statements
			}
			if (elseBody.get() instanceof Expression) {
              string = string.concat(elseBody.get().accept(new SymjaExpressionVisitor(),
						new SymjaIndentation(param.getIndentationLevel() + 1)));
				string = string.concat("\n");
			} else {
				string = string.concat(elseBody.get().accept(new SymjaStatementVisitor(),
						new SymjaIndentation(param.getIndentationLevel() + 1)));
			}
		}

		return string;
	}

	@Override
	public String visitImport(Import importElement, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("import ");

		List<Alias> names = importElement.getNames();
		if (names == null || names.isEmpty()) {
			throw new Python3ParserException("'Import' has no aliases.");
		}

		for (int i = 0; i < names.size(); i++) {
			string = string.concat(names.get(i).accept(new SymjaAliasVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));

			if (i != names.size() - 1) {
				string = string.concat(", ");
			}
		}

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitImportFrom(ImportFrom importFrom, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());

		Optional<Identifier> module = importFrom.getModule();
		List<Alias> names = importFrom.getNames();
		int level = importFrom.getLevel().intValue();

		if (module.isPresent() || level > 0) {
			string = string.concat("from ");

			for (int i = 1; i <= level; i++) {
				string = string.concat(".");
			}

			if (module.isPresent()) {
				string = string.concat(module.get().getName() + " ");
			}
		}

		string = string.concat("import ");
		
		if (names == null || names.isEmpty()) {
			throw new Python3ParserException("'ImportFrom' has no aliases.");
		}

		for (int i = 0; i < names.size(); i++) {
			string = string.concat(names.get(i).accept(new SymjaAliasVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));

			if (i != names.size() - 1) {
				string = string.concat(", ");
			}
		}

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitNonlocal(Nonlocal nonlocal, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("nonlocal ");

		List<Identifier> names = nonlocal.getNames();
		if (names == null || names.isEmpty()) {
			throw new Python3ParserException("'Nonlocal' has no names.");
		}

		for (int i = 0; i < names.size(); i++) {
			string = string.concat(names.get(i).getName());
			if (i != names.size() - 1) {
				string = string.concat(", ");
			}
		}

		string = string.concat("\n");

		return string;
	}

	@Override
	public String visitPass(Pass pass, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("pass");
		string = string.concat("\n");
		return string;
	}

	@Override
	public String visitRaise(Raise raise, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("raise");

		Optional<Expression> exc = raise.getExc();
		Optional<Expression> cause = raise.getCause();

		if (exc.isPresent()) {
			string = string.concat(" ");
            string = string.concat(exc.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));

			if (cause.isPresent()) {
				string = string.concat(" from ");
                string = string.concat(cause.get().accept(new SymjaExpressionVisitor(),
						new SymjaIndentation(param.getIndentationLevel())));
			}
		}

		string = string.concat("\n");
		return string;
	}

	@Override
	public String visitReturn(Return returnElement, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());
		string = string.concat("return");

		Optional<Expression> value = returnElement.getValue();

		if (value.isPresent()) {
			string = string.concat(" ");
            string = string.concat(value.get().accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
		}

		string = string.concat("\n");
		return string;
	}

	@Override
	public String visitTry(Try tryElement, SymjaIndentation param) {
		String string = new String();

		Statement body = tryElement.getBody();
		List<ExceptHandler> handlers = tryElement.getHandlers();
		List<Statement> handlersBody = tryElement.getHandlersBody();
		Optional<Statement> orElse = tryElement.getOrElse();
		Optional<Statement> finalBody = tryElement.getFinalBody();

		string = string.concat(param.getIndentationString());
		string = string.concat("try:");
		string = string.concat("\n");
		
		if (body == null) {
			throw new Python3ParserException("Body of 'Try' is empty.");
		}
		string = bodyToString(param, string, body);

		for (int i = 0; i < handlers.size(); i++) {
			string = string.concat(handlers.get(i).accept(new SymjaExceptHandlerVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));
			if (handlersBody.get(i) == null) {
				throw new Python3ParserException("Body of handler is empty.");
			}
			string = bodyToString(param, string, handlersBody.get(i));
		}

		if (orElse.isPresent()) {
			string = string.concat(param.getIndentationString());
			string = string.concat("else:");
			string = string.concat("\n");
			string = bodyToString(param, string, orElse.get());
		}

		if (finalBody.isPresent()) {
			string = string.concat(param.getIndentationString());
			string = string.concat("finally:");
			string = string.concat("\n");
			string = bodyToString(param, string, finalBody.get());
		}

		return string;
	}

	@Override
	public String visitWhile(While whileElement, SymjaIndentation param) {
		String string = new String();

		Expression test = whileElement.getTest();
		Statement body = whileElement.getBody();
		Optional<Statement> orElse = whileElement.getOrElse();

		string = string.concat(param.getIndentationString());
		string = string.concat("while ");
		if (test == null) {
			throw new Python3ParserException("'Test' in 'While' does not exist.");
		}
        string = string.concat(test.accept(new SymjaExpressionVisitor(),
				new SymjaIndentation(param.getIndentationLevel())));
		string = string.concat(":");
		string = string.concat("\n");
		
		if (body == null) {
			throw new Python3ParserException("Body of 'While' is empty.");
		}
		string = bodyToString(param, string, body);

		if (orElse.isPresent()) {
			string = string.concat(param.getIndentationString());
			string = string.concat("else:");
			string = string.concat("\n");
			string = bodyToString(param, string, orElse.get());
		}

		return string;
	}

	@Override
	public String visitWith(With with, SymjaIndentation param) {
		String string = new String();
		string = string.concat(param.getIndentationString());

		List<WithItem> items = with.getItems();
		Statement body = with.getBody();

		string = string.concat("with ");
		if (items == null || items.isEmpty()) {
			throw new Python3ParserException("'With' has no withItems.");
		}

		for (int i = 0; i < items.size(); i++) {
			string = string.concat(items.get(i).accept(new SymjaWithItemVisitor(),
					new SymjaIndentation(param.getIndentationLevel())));

			if (i != items.size() - 1) {
				string = string.concat(", ");
			}
		}

		string = string.concat(":");
		string = string.concat("\n");
		if (body == null) {
			throw new Python3ParserException("Body of 'With' is empty.");
		}
		string = bodyToString(param, string, body);
		string = string.concat("\n");

		return string;
	}

	private String bodyToString(SymjaIndentation param, String string, Statement body) {
		if (body instanceof Expression) {
			string = string.concat(param.getIndentationString() + "    ");
            string = string.concat(body.accept(new SymjaExpressionVisitor(),
					new SymjaIndentation(param.getIndentationLevel() + 1)));
			string = string.concat("\n");
		} else {
			string = string.concat(body.accept(new SymjaStatementVisitor(),
					new SymjaIndentation(param.getIndentationLevel() + 1)));
		}
		return string;
	}
}
