package com.example.bean;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

@Component
public class LdapProcessorImpl implements LdapProcessor{
	
	DirContext ctx;
	
	public void newConnection() {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://localhost:389");
		env.put(Context.SECURITY_PRINCIPAL, "cn=Manager, dc=maxcrc,dc=com");
		env.put(Context.SECURITY_CREDENTIALS, "secret");
		
		try {
			ctx = new InitialDirContext(env);
			System.out.println("Connection successful : "+ctx);
			System.out.println("");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean addUserInGroup(User user) {
		addUser(user);

		ModificationItem[] mods = new ModificationItem[1];
		Attribute attribute = new BasicAttribute("member", "cn="+user.getLanID()+",ou=users,ou="+user.getBranch()+",dc=maxcrc,dc=com");
		mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
		
		try {
			ctx.modifyAttributes("cn="+user.getAccessType()+",ou=groups,ou="+user.getBranch()+",dc=maxcrc,dc=com", mods);
			System.out.println("User added in Group");
			return true;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}
	private void addUser(User user) {

		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		
		attribute.add("inetOrgPerson");
		attributes.put(attribute);
		attributes.put("employeeNumber", user.getEmployeeId());
		attributes.put("sn", user.getLanID());
		attributes.put("mail", user.getMailId());
		attributes.put("givenName", user.getName());
		
		try {
			ctx.createSubcontext("cn="+user.getLanID()+",ou=users,ou="+user.getBranch()+",dc=maxcrc,dc=com", attributes);
			System.out.println("User Added");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public void sendMail() {
		System.out.println("......Sending mail..........");
		String to = "ashqkhan@dxc.com";
		String from = "ashqkhan@dxc.com";
		String host = "relay.csc.com";// or IP address

		// Get the session object
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.debug", "true");
		Session session = Session.getDefaultInstance(properties);

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			message.addRecipient(Message.RecipientType.BCC,
					new InternetAddress("ngaba2@csc.com"));
			
			message.setSubject("wmA Production Access Granted");
//			message.setText("Hi,\n\nYour access to wmA has been granted. The Production environment will be accessible via the following URL:"
//					+ "\nhttps://sts.manulife.com/adfs/ls/IdpInitiatedSignOn.aspx?RelayState=RPID%3Dhttp%253A%252F%252Fdxc.wma.jhlifeprod%26RelayState%3Dhttps%253A%252F%252Fjhlprd.csc-fsg.com%252Fwma%252Flogin.jsf%253Fenv%253DMAINFRAME%2520"
//					+ "\n\nRegards \nwmA Admin \n\n\n\n\n*** This is an automatically generated email, Please do not reply ***");
			message.setText("Hi,\n\nYour access to wmA has been granted. The Production environment will be accessible via the following URL:"
					+ "\nhttps://myapps.microsoft.com/signin/wma%20John%20Hancock/49812eff-d535-4533-bfa7-ecfc611cbac9?tenantId=5d3e2773-e07f-4432-a630-1a0f68a28a05&RelayState=https%253A%252F%252Fjhlprd.csc-fsg.com%252Fwma%252Flogin.jsf%253Fenv=MAINFRAME%2520"
					+ "\n\nRegards \nwmA Admin \n\n\n\n\n*** This is an automatically generated email, Please do not reply ***");

			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getGroups(String branch) {
		String usersContainer = "ou="+branch+",dc=maxcrc,dc=com";
		List<String> groupNames = new ArrayList<String>();
		try {
			SearchControls ctls = new SearchControls();
            String[] attrIDs = { "cn" };
            ctls.setReturningAttributes(attrIDs);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = ctx.search(usersContainer, "(objectclass=groupOfNames)",ctls);
            while (answer.hasMore()) {
                SearchResult rslt = (SearchResult) answer.next();
                Attributes attrs = rslt.getAttributes();
                String names = (attrs.get("cn")).toString();
                String name[] = names.split(":");
                groupNames.add(name[1]);
		}
          
		} catch (NamingException e) {
            e.printStackTrace();
        }
		return groupNames;

	
		
	}
	@Override
	public boolean removeUser(User user) throws NamingException {
		searchUserInGroup(user);
		
	try {
		ctx.destroySubcontext("cn="+user.getLanID()+",ou=users,ou="+user.getBranch()+",dc=maxcrc,dc=com");
		System.out.println("User deleted");
		return true;
	} catch (NamingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
		
}


	public void searchUserInGroup(User user ) throws NamingException {
	String userDN = "cn="+user.getLanID()+",ou=users,ou="+user.getBranch().trim()+",dc=maxcrc,dc=com";
	String searchFilter = "(|(objectclass=groupOfNames))"; 
	String[] reqAtt = { "member" , "cn" };
	SearchControls controls = new SearchControls();
	controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	controls.setReturningAttributes(reqAtt);
	NamingEnumeration users = ctx.search("dc=maxcrc,dc=com", searchFilter, controls);
	String member ="";
	SearchResult result = null;
	while (users.hasMore()) {
		String groupName = "";
		result = (SearchResult) users.next();
		Attributes attrs = result.getAttributes();
		    groupName = attrs.get("cn").toString();  
	   for (NamingEnumeration ae = attrs.getAll();ae.hasMore();){ 
	      Attribute attr = (Attribute)ae.next();                 
	        String id = attr.getID();
		    for (NamingEnumeration e = attr.getAll();e.hasMore();){                    
	         member = (String)e.next();		         	
		     if(userDN.equalsIgnoreCase(member))
		   	 removeUserfromGroup(user, groupName.replaceAll(": ", "="));
		          }
		        }
		       }      		
	}

private void removeUserfromGroup(User user, String groupName) {
	ModificationItem[] mods = new ModificationItem[1];
	Attribute attribute = new BasicAttribute("member", "cn="+user.getLanID()+",ou=users,ou="+user.getBranch()+",dc=maxcrc,dc=com");
	mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
	try {
		
		ctx.modifyAttributes(groupName+",ou=groups,ou=Manager,dc=maxcrc,dc=com", mods);
		System.out.println("User Deleted from "+groupName+" Group");
	} catch (NamingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
@Override
public List<String> getBranches() {
	List<String> branchNames = new ArrayList<String>();
	String usersContainer = "dc=maxcrc,dc=com";
	try {
		SearchControls ctls = new SearchControls();
        String[] attrIDs = { "ou" };
        ctls.setReturningAttributes(attrIDs);
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        NamingEnumeration<SearchResult> answer = ctx.search(usersContainer, "(objectclass=organizationalUnit)",ctls);
        while (answer.hasMore()) {
            SearchResult rslt = (SearchResult) answer.next();
            Attributes attrs = rslt.getAttributes();
            String names = (attrs.get("ou")).toString();
            String name[] = names.split(":");
            branchNames.add(name[1]);
	}
        ctx.close();
	} catch (NamingException e) {
        e.printStackTrace();
    }
	return branchNames;
	
}
@Override
public boolean addBranch(User user) {
	Attributes attributes = new BasicAttributes();
	Attribute attribute = new BasicAttribute("objectClass");
	
	attribute.add("organizationalUnit");
	attributes.put(attribute);
	attributes.put("ou", user.getBranch());
	
	
	try {
		ctx.createSubcontext("ou="+user.getBranch()+",dc=maxcrc,dc=com", attributes);
		System.out.println("Branch Added");
		boolean value = addSubGroupsForBranch(user);
		return true;
	} catch (NamingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}

}

private boolean addSubGroupsForBranch(User user) {
	Attributes attributes = new BasicAttributes();
	Attribute attribute = new BasicAttribute("objectClass");
	attribute.add("organizationalUnit");
	attribute.add("top");
	attributes.put(attribute);
	try {
		ctx.createSubcontext("ou=groups,ou="+user.getBranch()+",dc=maxcrc,dc=com", attributes);
		ctx.createSubcontext("ou=users,ou="+user.getBranch()+",dc=maxcrc,dc=com", attributes);
		return true;
	} catch (NamingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
}
@Override
public List<String> getMembers(String Branch) {
	String usersContainer = "ou="+Branch+",dc=maxcrc,dc=com";
	List<String> memberNames = new ArrayList<String>();
	try {
		SearchControls ctls = new SearchControls();
        String[] attrIDs = { "cn" };
        ctls.setReturningAttributes(attrIDs);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = ctx.search(usersContainer, "(objectclass=inetOrgPerson)",ctls);
        while (answer.hasMore()) {
            SearchResult rslt = (SearchResult) answer.next();
            Attributes attrs = rslt.getAttributes();
            String names = (attrs.get("cn")).toString();
            String name[] = names.split(":");
            memberNames.add(name[1]);
	}
      
	} catch (NamingException e) {
        e.printStackTrace();
    }
	return memberNames;


}
@SuppressWarnings("deprecation")
@Override
public boolean executeFromExcel(String branch, String fileName) throws Exception {
	boolean value = false;
	User user = new User();
	user.setBranch(branch.trim());
	FileInputStream fis=new FileInputStream(new File("C:\\OpenLDAP\\"+fileName));  
	HSSFWorkbook wb=new HSSFWorkbook(fis);   
	Sheet sheet=wb.getSheetAt(0);  
	Iterator rowiterator = sheet.iterator();
	while(rowiterator.hasNext()) {
		Row row = (Row) rowiterator.next();
		if(row.getRowNum() == 0)
			continue;
		else {
		Iterator cellIterator = row.cellIterator();	
		while(cellIterator.hasNext()) {
			Cell cell = (Cell) cellIterator.next();
			if(cell.getCellTypeEnum() == CellType.STRING) {
				if(cell.getColumnIndex() == 0) {
				user.setLanID(cell.getStringCellValue());
				user.setSn(cell.getStringCellValue());}
				else if(cell.getColumnIndex() == 1)
				user.setAccessType(cell.getStringCellValue());
				else if(cell.getColumnIndex() == 3)
					user.setName(cell.getStringCellValue());
				else if(cell.getColumnIndex() == 4)
					user.setMailId(cell.getStringCellValue());
			}
			else if(cell.getCellTypeEnum() == CellType.NUMERIC) {
				int temp = (int) cell.getNumericCellValue();
				user.setEmployeeId(Integer.toString(temp));
			}
			
		}
		System.out.println(user);
		if(user.getAccessType().equalsIgnoreCase("Remove")) {
			if(removeUser(user))
				value = true;
			else {
				value = false;
			break ;
			}
		}
			
		else {
			if(addUserInGroup(user)) 
				value = true;
			else {
				value = false;
			break ;
			}
		}
			
		
		
	}
	
	}
			return value;	
}

}

