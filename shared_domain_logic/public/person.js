function Person(firstName, lastName, age) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = Number.parseInt(age);
    this.errors = {};

    if (!this.firstName) {
        this.errors.firstName = 'cannot be empty';
    }
    if (!this.lastName) {
        this.errors.lastName = 'cannot be empty';
    }
    if (Number.isNaN(this.age)) {
        this.errors.age = 'has to be integer';
    } else {
        if (age < 0) {
            this.errors.age = 'has to be positive';
        }
        if (age > 150) {
            this.errors.age = 'has to be less than 150';
        }
    }

    Object.freeze(this);
}

Person.prototype.isInvalid = function () {
    return Object.keys(this.errors).length > 0;
};

Person.prototype.update = function (attributes) {
    return new Person(
        ('firstName' in attributes ? attributes : this).firstName,
        ('lastName' in attributes ? attributes : this).lastName,
        ('age' in attributes ? attributes : this).age);
};

Person.fromObject = function (object) {
    return new Person(object['firstName'], object['lastName'], object['age'])
};


