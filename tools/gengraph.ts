import { State, STATE_DATA } from '../lib/rc/video'

// Extra info

const extraTransitions: { [state: number]: State[] } = {
    [State.PIXCODE_OR_RGB]: [ State.PIXCODE_0, State.PIXCODE_1B, State.PIXCODE_2B, State.PIXCODE_3B, State.PIXCODE_4B ],
    [State.COMMAND_MORE]: [ State.STRING_START, State.N_37 ],
    [State.PIXEL_FILL2]: [ State.PIXEL_FILL_PLUS8, State.PIXEL_FILL_N ],
}

const subgraphs: { id: string, states: State[] }[] = [
    { id: 'pixcode', states: [ State.PIXCODE_0, State.PIXCODE_1, State.PIXCODE_1B, State.PIXCODE_2B, State.PIXCODE_3B, State.PIXCODE_4B ] },
    { id: 'rgb', states: [ State.BEGIN_RGB, State.SET_RED, State.SET_GREEN, State.SET_BLUE, State.SET_GRAYSCALE ] },
    { id: 'pixel_fill', states: [ State.PIXEL_FILL, State.PIXEL_FILL2, State.PIXEL_FILL_1, State.PIXEL_FILL_N, State.PIXEL_FILL_PLUS8 ] },
    { id: 'advance_blocks', states: [ State.ADVANCE_BLOCKS, State.ADVANCE_BLOCKS2, State.ADVANCE_BLOCKS_1, State.ADVANCE_BLOCKS_N, State.ADVANCE_BLOCKS_PLUS2 ] },
    //{ id: 'other', states: [ State.OTHER, State.OTHER1, State.OTHER2, State.OTHER3, State.OTHER4 ] },
    { id: 'set_position', states: [ State.SET_POSITION, State.SET_POSITION_Y ] },
    { id: 'set_x', states: [ State.SET_X, State.SET_X_ABSOLUTE, State.SET_X_RELATIVE ] },
    { id: 'set_screen_size', states: [ State.SET_WIDTH, State.SET_HEIGHT, State.SET_YCLIPPED ] },
    { id: 'command', states: [ State.COMMAND_READ, State.COMMAND_MORE ] },
    { id: 'string', states: [ State.STRING_START, State.STRING_READ ] },
]

const blackList = new Set([ State.FATAL_ERROR, State.DISCARD_QUEUE ])
const ghostTargets = [ State.BEGIN, State.PIXEL_BEGIN ]
const ghostTargetsExcluded = new Set([ State.RESET ].concat(ghostTargets))



console.log(`
digraph G {
  rankdir="LR"
  graph [ nodesep="0.2" ]
  node [ fontname="Roboto" ]
  edge [ fontname="Roboto" ]

`)

for (const subgraph of subgraphs) {
    const color = `"#227700"`
    console.log(`  subgraph cluster_${subgraph.id} {`)
    console.log(`  color=${color}; fontcolor=${color}`)
    console.log(`  margin=20`)
    console.log(`  fontname="Roboto"`)
    console.log(`  label=<<b>${subgraph.id.toUpperCase()}</b>>`)
    console.log(`}`)
}

console.log()

const states: State[] = Object.keys(STATE_DATA).map(x => parseInt(x))
    .filter(x => !blackList.has(x))
const getId = (state: State) => State[state]

for (const state of states) {
    const label = `<b>${State[state]}</b> [${state}]`
    const attributes = [`label=<${label}>`]
    if (ghostTargets.includes(state))
        attributes.push('penwidth=3')
    const nodeDef = `${getId(state)} [${attributes.join(', ')}]`
    const subgraph = subgraphs.find(x => x.states.includes(state))
    if (subgraph) {
        console.log(`  subgraph cluster_${subgraph.id} { ${nodeDef} }`)
    } else {
        console.log(`  ${nodeDef}`)
    }
}

for (const state of ghostTargets) {
    let label = `back to<br/><b>${State[state]}</b>`
    if (state === State.PIXEL_BEGIN)
        label = `back to<br/><b>PIXEL_BEGIN</b><br/>or <b>BEGIN</b>`
    const attributes = [
        `label=<${label}>`,
        `style=filled`,
        `fill="#aaaaaa"`,
    ]
    console.log(`${getId(state)}__TARGET [${attributes.join(', ')}]`)
}

console.log()

for (const state of states) {
    const data = STATE_DATA[state]
    const targets: {
        state: State,
        kind: 'unconditional' | 'nz' | 'zero' | 'one' | 'extra'
    }[] = []
    if (data.next0 === undefined || data.next0 === data.next) {
        targets.push({ state: data.next, kind: 'unconditional' })
    } else {
        targets.push({ state: data.next, kind: data.bits > 1 ? 'nz' : 'one' })
        targets.push({ state: data.next0, kind: 'zero' })
    }
    for (const s of extraTransitions[state] || []) {
        targets.push({ state: s, kind: 'extra' })
    }
    
    for (const target of targets) {
        const label = { unconditional: '', nz: '≠ 0', zero: '0', one: '1', extra: '' }[target.kind]
        const attributes = [ `label=<${label}>` ]
        if (target.kind === 'extra')
            attributes.push('style=dashed')
        let targetNode = getId(target.state)
        if (!ghostTargetsExcluded.has(state) && ghostTargets.includes(target.state)) {
            targetNode += '__TARGET'
        }
        console.log(`  ${getId(state)} -> ${targetNode} [${attributes.join(', ')}]`)
    }
}

console.log('\n}')
