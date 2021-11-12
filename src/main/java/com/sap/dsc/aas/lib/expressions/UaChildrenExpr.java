package com.sap.dsc.aas.lib.expressions;

import com.sap.dsc.aas.lib.mapping.TransformationContext;
import com.sap.dsc.aas.lib.transform.XPathHelper;
import com.sap.dsc.aas.lib.ua.transform.BrowsepathXPathBuilder;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;
import java.util.stream.Collectors;

public class UaChildrenExpr implements Expression {

    protected final List<Expression> args;

    public UaChildrenExpr( List<Expression> args){
        this.args = args;
    }

    @Override
    public List<Element> evaluate(TransformationContext ctx) {
        if(!ctx.getContextItem().isPresent()
                || !(ctx.getContextItem().get() instanceof Node)) {
            throw new IllegalArgumentException("no Node Context is given.");
        }
        List<String> path = args.stream().map(arg -> arg.evaluate(ctx)).filter(val -> val instanceof String).map(val -> (String) val).collect(Collectors.toList());
        String[] pathElems = new String[path.size()];
        String pathExp = BrowsepathXPathBuilder.getInstance().pathExpression(path.toArray(pathElems));
        List<Node> nodes = XPathHelper.getInstance().getNodes((Node) ctx.getContextItem().get(), pathExp);
        if(nodes != null && nodes.size() == 1 &&  nodes.get(0) instanceof  Element){
            return ((Element) nodes.get(0)).elements();
        }
        else{
            throw new IllegalArgumentException("@uaBrowsePath should be array of path elements as String, and should match exactly one UaNode.");
        }
    }
}
