package wang.xiaoluobo.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * https://leetcode-cn.com/problems/unique-email-addresses/
 */
public class Problem0929 {

    public static void main(String[] args) {
        Problem0929 problem0929 = new Problem0929();
        String[] emails = new String[]{"test.email+alex@leetcode.com", "test.e.mail+bob.cathy@leetcode.com", "testemail+david@lee.tcode.com"};
        System.out.println(problem0929.numUniqueEmails(emails));
    }

    public int numUniqueEmails(String[] emails) {
        Set<String> set = new HashSet();
        String domain = null;
        String localName = null;
        for (String email: emails){
            int index = email.indexOf("@");
            domain = email.substring(index);
            localName = email.substring(0, index);

            if(null == localName || "".equals(localName)){
                continue;
            }

            int n = localName.indexOf("+");
            if(n != -1){
                localName = localName.substring(0, n);
            }

            String str = localName.replaceAll("\\.", "");
            set.add(str + domain);
        }
        return set.size();
    }
}
