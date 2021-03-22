package com.taveeshsharma.requesthandler.orchestration;

public class ColorAssignment {
    Integer start;
    Integer end;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public ColorAssignment(Integer start, Integer end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" +
                "s=" + start +
                ", e=" + end +
                '}';
    }
}
