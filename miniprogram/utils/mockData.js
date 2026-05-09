const mockCategories = [
  { id: 1, name: '招牌烤鱼', icon: '🍲' },
  { id: 2, name: '精品凉菜', icon: '🥗' },
  { id: 3, name: '主食', icon: '🍚' },
  { id: 4, name: '酒水饮料', icon: '🍹' }
]

const mockDishes = [
  {
    id: 1,
    name: '香辣烤鱼',
    categoryId: 1,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=spicy%20grilled%20fish%20with%20red%20peppers%20and%20vegetables%20on%20wooden%20plate%20food%20photography&image_size=square',
    description: '鲜嫩鱼肉配以秘制香辣酱料，香气扑鼻',
    flavors: '麻辣,酸菜,蒜香',
    price: 88,
    status: 1
  },
  {
    id: 2,
    name: '蒜香烤鱼',
    categoryId: 1,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=garlic%20grilled%20fish%20with%20fresh%20herbs%20on%20plate%20food%20photography&image_size=square',
    description: '蒜香浓郁，鱼肉嫩滑',
    flavors: '蒜香,微辣,原味',
    price: 88,
    status: 1
  },
  {
    id: 3,
    name: '凉拌黄瓜',
    categoryId: 2,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=chinese%20cold%20cucumber%20salad%20with%20garlic%20and%20vinegar%20food%20photography&image_size=square',
    description: '清爽可口，开胃小菜',
    flavors: '',
    price: 12,
    status: 1
  },
  {
    id: 4,
    name: '夫妻肺片',
    categoryId: 2,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=sichuan%20beef%20slices%20in%20spicy%20sauce%20chinese%20appetizer%20food%20photography&image_size=square',
    description: '麻辣鲜香，经典川菜',
    flavors: '麻辣,微辣',
    price: 28,
    status: 1
  },
  {
    id: 5,
    name: '蛋炒饭',
    categoryId: 3,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=chinese%20egg%20fried%20rice%20with%20scallions%20food%20photography&image_size=square',
    description: '粒粒分明，蛋香浓郁',
    flavors: '',
    price: 18,
    status: 1
  },
  {
    id: 6,
    name: '白米饭',
    categoryId: 3,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=steamed%20white%20rice%20in%20bowl%20food%20photography&image_size=square',
    description: '软糯香甜',
    flavors: '',
    price: 3,
    status: 1
  },
  {
    id: 7,
    name: '可乐',
    categoryId: 4,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cola%20drink%20in%20glass%20with%20ice%20cubes%20food%20photography&image_size=square',
    description: '冰爽可乐',
    flavors: '',
    price: 8,
    status: 1
  },
  {
    id: 8,
    name: '王老吉',
    categoryId: 4,
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=herbal%20tea%20drink%20in%20red%20can%20food%20photography&image_size=square',
    description: '清凉降火',
    flavors: '',
    price: 6,
    status: 1
  }
]

const mockCombos = [
  {
    id: 1,
    name: '双人套餐',
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=chinese%20food%20set%20meal%20for%20two%20with%20fish%20and%20dishes%20food%20photography&image_size=square',
    description: '香辣烤鱼+凉菜+主食+饮料',
    price: 168,
    status: 1,
    items: [
      { dishId: 1, name: '香辣烤鱼', quantity: 1 },
      { dishId: 3, name: '凉拌黄瓜', quantity: 1 },
      { dishId: 5, name: '蛋炒饭', quantity: 2 },
      { dishId: 7, name: '可乐', quantity: 2 }
    ]
  },
  {
    id: 2,
    name: '四人套餐',
    image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=chinese%20food%20set%20meal%20for%20four%20with%20multiple%20dishes%20food%20photography&image_size=square',
    description: '两条烤鱼+多种配菜',
    price: 298,
    status: 1,
    items: [
      { dishId: 1, name: '香辣烤鱼', quantity: 1 },
      { dishId: 2, name: '蒜香烤鱼', quantity: 1 },
      { dishId: 3, name: '凉拌黄瓜', quantity: 1 },
      { dishId: 4, name: '夫妻肺片', quantity: 1 },
      { dishId: 6, name: '白米饭', quantity: 4 },
      { dishId: 8, name: '王老吉', quantity: 4 }
    ]
  }
]

module.exports = {
  mockCategories,
  mockDishes,
  mockCombos
}
