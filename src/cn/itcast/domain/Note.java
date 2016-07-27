package cn.itcast.domain;

import com.google.gdata.data.douban.NoteEntry;

public class Note {

	private String title;
	private String content;
	private String pubDateTime;
	private String privacy;
	private String canReply;
	private NoteEntry noteEntry;

	public NoteEntry getNoteEntry() {
		return noteEntry;
	}

	public void setNoteEntry(NoteEntry noteEntry) {
		this.noteEntry = noteEntry;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPubDateTime() {
		return pubDateTime;
	}

	public void setPubDateTime(String pubDateTime) {
		this.pubDateTime = pubDateTime;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public String getCanReply() {
		return canReply;
	}

	public void setCanReply(String canReply) {
		this.canReply = canReply;
	}

}
