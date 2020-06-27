package com.keepnote.colorpicker;


class ColorPal {
    private int color;
    private boolean check;

    ColorPal(int color) {
        this.color = color;
        this.check = false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ColorPal && ((ColorPal) o).color == color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}