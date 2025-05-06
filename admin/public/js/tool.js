Tool = {
  /**
   * Null validation: return true when null or ""
   */
  isEmpty: (obj) => {
    if ((typeof obj === 'string')) {
      return !obj || obj.replace(/\s+/g, "") === ""
    } else {
      return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
    }
  },

  /**
   * Not Empty validation
   */
  isNotEmpty: (obj) => {
    return !Tool.isEmpty(obj);
  },

  /**
   * Object copy
   * @param obj
   */
  copy: (obj) => {
    if (Tool.isNotEmpty(obj)) {
      return JSON.parse(JSON.stringify(obj));
    }
  },

  /**
   * Convert array to tree structure using recursion
   * The field of Parent ID is parent
   */
  array2Tree: (array, parentId) => {
    if (Tool.isEmpty(array)) {
      return [];
    }

    const result = [];
    for (let i = 0; i < array.length; i++) {
      const c = array[i];
      // console.log(Number(c.parent), Number(parentId));
      if (Number(c.parent) === Number(parentId)) {
        result.push(c);

        // Get child nodes of current node Recursively
        const children = Tool.array2Tree(array, c.id);
        if (Tool.isNotEmpty(children)) {
          c.children = children;
        }
      }
    }
    return result;
  },

  /**
   * Generate [radix] base number with [len] length randomly
   * @param len
   * @param radix default value is 62
   * @returns {string}
   */
  uuid: (len, radix = 62) => {
    const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    const uuid = [];
    radix = radix || chars.length;

    for (let i = 0; i < len; i++) {
      uuid[i] = chars[0 | Math.random() * radix];
    }

    return uuid.join('');
  }
};
