class Hero extends Character {
    private int maxHp;

    public Hero(String name, int hp, int attack) {
        super(name, hp, attack);
        this.maxHp = hp; // store max HP
    }

    public int getMaxHp() { return maxHp; }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (getHp() > maxHp) setHp(maxHp); // prevent overheal
    }

    private void setHp(int hp) {
        try {
            java.lang.reflect.Field field = Character.class.getDeclaredField("hp");
            field.setAccessible(true);
            field.setInt(this, hp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
