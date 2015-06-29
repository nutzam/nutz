import com.sun.javadoc.*;

/**
 * 这个类需要加入jdk目录下的tools.jar才能编译
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class NutDoclet extends Doclet {
    
    static int count ;

    public static boolean start(RootDoc root) {
        ClassDoc[] classes = root.classes();
        for (int i = 0; i < classes.length; ++i) {
            ClassDoc cd = classes[i];
            if (cd.typeName().toLowerCase().contains("test"))
                continue;
            printMembers(cd.constructors());
            printMembers(cd.methods());
        }
        return true;
    }

    static void printMembers(ExecutableMemberDoc[] mems) {
        for (ExecutableMemberDoc _doc : mems) {
            if (_doc.qualifiedName().startsWith("test_"))
                continue;
            if (_doc.getRawCommentText() == null || _doc.getRawCommentText().trim().length() == 0) {
                System.out.println(count + ">> " + _doc.containingClass().qualifiedTypeName() + "#" + _doc.qualifiedName() + _doc.signature());
                count ++;
            }
        }
    }        
}
