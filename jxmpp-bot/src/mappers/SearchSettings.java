package mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import domain.syslog.SyslogSession;

public class SearchSettings {

	public SearchSettings() {
		senders = new ArrayList<String>();
		types = new ArrayList<String>();
		categories = new ArrayList<String>();
		sessions = new ArrayList<SyslogSession>();
	}

	public void addSenders(Collection<String> collection) {
		if (collection != null) {
			for (String s : collection) {
				addSender(s);
			}
		}
	}

	public void addTypes(Collection<String> collection) {
		if (collection != null) {
			for (String t : collection) {
				addType(t);
			}
		}
	}

	public void addCategories(Collection<String> collection) {
		if (collection != null) {
			for (String c : collection) {
				addCategory(c);
			}
		}
	}

	public void addSessions(Collection<SyslogSession> collection) {
		if (collection != null) {
			for (SyslogSession s : collection) {
				addSession(s);
			}
		}
	}

	public void addSender(String sender) {
		if (sender != null) {
			if (!senders.contains(sender)) {
				senders.add(sender);
			}
		}
	}

	public void addType(String type) {
		if (type != null) {
			if (!types.contains(type)) {
				types.add(type);
			}
		}
	}

	public void addCategory(String category) {
		if (!categories.contains(category)) {
			categories.add(category);
		}
	}

	public void addSession(SyslogSession session) {
		if (session != null) {
			if (findSession(session) == null) {
				sessions.add(session);
			}
		}
	}

	public void removeSender(String sender) {
		if (sender != null) {
			senders.remove(sender);
		}
	}

	public void removeType(String type) {
		if (type != null) {
			types.remove(type);
		}
	}

	public void removeCategory(String category) {
		if (category != null) {
			categories.remove(category);
		}
	}

	public void removeSession(SyslogSession session) {
		if (session != null) {
			SyslogSession found = findSession(session);

			if (found != null) {
				sessions.remove(found);
			}
		}
	}

	public void clearCategories() {
		categories.clear();
	}

	public void clearSenders() {
		senders.clear();
	}

	public void clearTypes() {
		types.clear();
	}

	public void clearSessisons() {
		sessions.clear();
	}

	public void setStartDate(Date value) {
		startDate = value;
	}

	public void setEndDate(Date value) {
		endDate = value;
	}

	public void setTextPattern(String textPattern) {
		msgText = textPattern;
	}

	public List<String> getSenders() {
		return getCopy(senders);
	}

	public List<String> getTypes() {
		return getCopy(types);
	}

	public List<String> getCategories() {
		return getCopy(categories);
	}

	public List<SyslogSession> getSessions() {
		return new ArrayList<SyslogSession>(sessions);
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getTextPattern() {
		return msgText;
	}

	/**
	 * Finds given session inside internal sessions collection. Comparison is
	 * made using ID attribute (not by equals())
	 * 
	 * @param session
	 *            Session to be found inside internal sessions collection
	 * @return Null reference if no such as session found, SyslogSession
	 *         otherwise
	 */
	protected SyslogSession findSession(SyslogSession session) {
		SyslogSession result = null;

		if (session != null) {
			long sid = session.getID();
			for (SyslogSession s : sessions) {
				if (s.getID() == sid) {
					result = s;
					break;
				}
			}
		}
		return result;
	}

	private List<String> getCopy(List<String> source) {
		ArrayList<String> result = null;

		if (source != null) {
			result = new ArrayList<String>(source);
		}

		return result;
	}

	List<String> senders;
	List<String> types;
	List<String> categories;
	List<SyslogSession> sessions;
	Date startDate, endDate;
	String msgText;

}
