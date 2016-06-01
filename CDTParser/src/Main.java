import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTExpressionStatement;
import org.eclipse.core.runtime.CoreException;

public class Main {

	public static void main(String[] args) throws CoreException {
		parse("C:\\Users\\guga\\Desktop\\code.c");
	}
	
	public static void parse(String str) throws CoreException{
		FileContent fileContent = FileContent.createForExternalFileLocation(str);
		
		Map<String, String> definedSymbols = new HashMap<String, String>();
		String[] includePaths = new String[0];
		IScannerInfo info = new ScannerInfo(definedSymbols, includePaths);
		IParserLogService log = new DefaultLogService();

		IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();

		int opts = 8;
		IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info, emptyIncludes, null, opts, log);
		
		ASTVisitor visitor = new ASTVisitor(true) {
	        public int visit(IASTName name) {
	            String prtName = name.toString();
	            if (prtName.length() == 0)
	                prtName = name.getRawSignature(); // use pre pre-processor
	            // value
	            //System.out.println("Visiting name: " + prtName);
	            return PROCESS_CONTINUE;
	        }
	        
	        public int visit(IASTStatement stmt) { // lots more
	            String sig = stmt.getRawSignature();
	            if (sig.length() > 0)
	                System.out.println("Visiting stmt: "
	                        + stmt.getRawSignature());
	            else if (stmt instanceof IASTCompoundStatement) {
	                IASTCompoundStatement cstmt = (IASTCompoundStatement) stmt;
	                IASTStatement[] stmts = cstmt.getStatements();
	                System.out.println("Visiting compound stmt with stmts: "
	                        + stmts.length);
	                for (IASTStatement st : stmts) {
	                    String rawSig = st.getRawSignature();

	                    if (rawSig.length() == 0) {
	                        System.out.println("   ->" + st);
	                        if (st instanceof CASTExpressionStatement) {
	                            CASTExpressionStatement es = (CASTExpressionStatement) st;
	                            IASTExpression exp = es.getExpression();
	                            if (exp instanceof IASTBinaryExpression) {
	                                IASTBinaryExpression bexp = (IASTBinaryExpression) exp;

	                                System.out.println("    binary exp: "
	                                        + bexp.getOperand1() + " "
	                                        + bexp.getOperator() + " "
	                                        + bexp.getOperand2());
	                            }
	                        }
	                    } else {
	                        System.out.println("   ->" + rawSig);
	                    }
	                }
	            }
	            return PROCESS_CONTINUE;
	        }
	        
	        public int visit(IASTDeclaration decl) {

	            //System.out.println("Visiting decl: " + decl.getRawSignature());
	            return PROCESS_CONTINUE;
	        }
		};
		
	    translationUnit.accept(visitor);
	}
}
